package org.example.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.*;
import org.example.entity.exception.UserOperationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;
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
            Consumer<E> customConverter) {

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

    /**
     * 通用分页查询方法
     *
     * @param vo               查询条件VO
     * @param pageNumGetter    获取pageNum的函数
     * @param pageSizeGetter   获取pageSize的函数
     * @param service          服务接口
     * @param fieldSelector    字段选择函数
     * @param conditionBuilder 条件构建函数
     * @param voConverter      实体转VO转换器
     * @param <T>              实体类型
     * @param <V>              查询VO类型
     * @param <R>              结果VO类型
     * @return 分页查询结果
     */
    public static <T, V, R> IPage<R> queryByConditions(
            V vo,
            Function<V, Long> pageNumGetter,
            Function<V, Long> pageSizeGetter,
            IService<T> service,
            Function<LambdaQueryWrapper<T>, LambdaQueryWrapper<T>> fieldSelector,
            BiConsumer<V, LambdaQueryWrapper<T>> conditionBuilder,
            Function<T, R> voConverter) {

        // 1.构建分页对象
        Page<T> page = new Page<>(
                pageNumGetter.apply(vo),
                pageSizeGetter.apply(vo),
                true
        );

        // 2.构建动态查询条件
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper = fieldSelector.apply(wrapper);

        // 3.应用查询条件
        conditionBuilder.accept(vo, wrapper);

        // 4.执行查询并转换结果
        return service.page(page, wrapper).convert(voConverter);
    }

    /**
     * 通用条件构建器
     */
    public static <T, V> void buildCommonConditions(
            V vo,
            LambdaQueryWrapper<T> wrapper,
            // 值获取函数
            Function<V, Integer> idGetter,
            Function<V, String> usernameGetter,
            Function<V, String> emailGetter,
            Function<V, String> phoneGetter,
            Function<V, String> addressGetter,
            Function<V, Short> sexGetter,
            Function<V, LocalDate> startBirthGetter,
            Function<V, LocalDate> endBirthGetter,
            Function<V, LocalDateTime> startCreateTimeGetter,
            Function<V, LocalDateTime> endCreateTimeGetter,
            Function<V, LocalDateTime> startUpdateTimeGetter,
            Function<V, LocalDateTime> endUpdateTimeGetter,
            // 字段获取函数
            SFunction<T, ?> idField,
            SFunction<T, ?> usernameField,
            SFunction<T, ?> emailField,
            SFunction<T, ?> phoneField,
            SFunction<T, ?> addressField,
            SFunction<T, ?> sexField,
            SFunction<T, ?> birthField,
            SFunction<T, ?> createTimeField,
            SFunction<T, ?> updateTimeField,
            // 排序相关函数
            Function<V, String> sortFieldGetter,
            Function<V, Boolean> sortAscGetter,
            Function<String, SFunction<T, ?>> sortFunction) {

        // ID条件
        if (idGetter.apply(vo) != null) {
            wrapper.likeRight(idField, idGetter.apply(vo));
        }

        // 用户名条件
        if (StringUtils.isNotBlank(usernameGetter.apply(vo))) {
            wrapper.like(usernameField, usernameGetter.apply(vo));
        }

        // 邮箱条件
        if (StringUtils.isNotBlank(emailGetter.apply(vo))) {
            wrapper.eq(emailField, emailGetter.apply(vo));
        }

        // 电话条件
        if (StringUtils.isNotBlank(phoneGetter.apply(vo))) {
            wrapper.likeRight(phoneField, phoneGetter.apply(vo));
        }

        // 地址条件
        if (StringUtils.isNotBlank(addressGetter.apply(vo))) {
            wrapper.like(addressField, addressGetter.apply(vo));
        }

        // 性别条件
        if (sexGetter.apply(vo) != null) {
            wrapper.eq(sexField, sexGetter.apply(vo));
        }

        // 生日范围
        LocalDate startBirth = startBirthGetter.apply(vo);
        LocalDate endBirth = endBirthGetter.apply(vo);
        if (startBirth != null && endBirth != null) {
            wrapper.between(birthField, startBirth, endBirth);
        }

        // 创建时间范围
        LocalDateTime startCreateTime = startCreateTimeGetter.apply(vo);
        LocalDateTime endCreateTime = endCreateTimeGetter.apply(vo);
        if (startCreateTime != null && endCreateTime != null) {
            wrapper.between(createTimeField, startCreateTime, endCreateTime);
        }

        // 更新时间范围
        LocalDateTime startUpdateTime = startUpdateTimeGetter.apply(vo);
        LocalDateTime endUpdateTime = endUpdateTimeGetter.apply(vo);
        if (startUpdateTime != null && endUpdateTime != null) {
            wrapper.between(updateTimeField, startUpdateTime, endUpdateTime);
        }

        // 排序
        wrapper.orderBy(true, sortAscGetter.apply(vo),
                sortFunction.apply(sortFieldGetter.apply(vo)));
    }

}
