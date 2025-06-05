package org.example.utils;

import org.example.entity.*;
import org.example.entity.exception.UserOperationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 用户类实体操作工具
 * 专门用于用户注册、验证和保存等操作
 *
 * @author hwshou
 * @date 2025/6/4  22:25
 */
public class UserEntityUtils {

    /**
     * 添加用户实体
     *
     * @param userVO          用户VO对象（实现UserVerifiable）
     * @param entityClass     用户实体类类型
     * @param context         用户实体操作上下文
     * @param redisTemplate   Redis模板
     * @param passwordEncoder 密码编码器
     * @param customLogic     自定义逻辑处理
     * @return 错误消息或null（成功）
     */
    public static <V extends UserVerifiable, E extends BaseUserEntity> String addUserEntity(
            V userVO,
            Class<E> entityClass,
            UserEntityContext<E> context,
            StringRedisTemplate redisTemplate,
            PasswordEncoder passwordEncoder,
            Consumer<E> customLogic) {

        // 1. 验证用户信息
        String error = verifyUserRegistration(userVO, context, redisTemplate);
        if (error != null) return error;

        try {
            // 2. 将VO转换为用户实体
            E userEntity = userVO.asDTO(entityClass, dto -> {
                // 3. 应用自定义逻辑
                if (customLogic != null) customLogic.accept(dto);

                // 4. 设置用户公共字段
                dto.setRegisterTime(LocalDateTime.now());
                dto.setUpdateTime(LocalDateTime.now());
            });

            // 5. 保存用户实体
            if (context.saveUser(userEntity)) {
                // 清除验证码缓存
                redisTemplate.delete(Const.VERIFY_EMAIL_DATA + userVO.getEmail());
                return null; // 成功
            } else {
                return "内部错误，保存用户失败";
            }
        } catch (Exception e) {
            throw new UserOperationException("用户实体转换失败", e);
        }
    }

    /**
     * 验证用户注册信息
     *
     * @param userVO        用户VO对象
     * @param context       用户实体操作上下文
     * @param redisTemplate Redis模板
     * @return 用户是否已被注册
     */
    private static <V extends UserVerifiable, E extends BaseUserEntity> String verifyUserRegistration(
            V userVO,
            UserEntityContext<E> context,
            StringRedisTemplate redisTemplate) {

        String email = userVO.getEmail();
        String username = userVO.getUsername();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = redisTemplate.opsForValue().get(key);

        if (code == null) return "请先获取验证码";
        if (!code.equals(userVO.getCode())) return "验证码输入错误";
        if (context.existsUserEmail(email)) return "邮箱已被注册";
        if (context.existsUsername(username)) return "用户名已被占用";
        return null;
    }

    /**
     * 通用更新用户实体方法
     *
     * @param updateVO        更新VO对象
     * @param entityClass     实体类类型
     * @param context         用户更新上下文
     * @param emailGetter     从VO中获取邮箱的函数
     * @param customConverter 自定义转换逻辑（VO转实体）
     * @return 错误消息或null（成功）
     */
    public static <V extends DTOConverter, E extends BaseUserEntity> String updateUserEntity(
            V updateVO,
            Class<E> entityClass,
            UserUpdateContext<E> context,
            Function<V, String> emailGetter,
            Consumer<E> customConverter){

        // 1. 参数校验
        Integer id = context.getIdGetter().apply(updateVO);
        if (id == null) {
            return "id不能为空";
        }
        // 2. 查询现有实体
        E existingEntity = context.getUserById(id);
        if (existingEntity == null) {
            return "账户不存在";
        }

        // 3. 检查邮箱冲突（排除自身）
        String newEmail = emailGetter.apply(updateVO);
        if (newEmail != null && !newEmail.equals(existingEntity.getEmail())) {
            if (context.existsUserEmailExcludingId(newEmail, id)) {
                return "此邮箱已被其他用户注册";
            }
        }

        try {
            // 4. 安全转换VO到实体
            E updatedEntity = updateVO.asDTO(entityClass, entity -> {
                // 应用自定义转换逻辑
                if (customConverter != null) customConverter.accept(entity);
                // 设置更新时间
                entity.setUpdateTime(LocalDateTime.now());
            });

            // 5. 执行更新
            return context.updateUser(updatedEntity) ? null : "更新失败";
        } catch (Exception e) {
            throw new UserOperationException("实体转换失败", e);
        }

    }

}
