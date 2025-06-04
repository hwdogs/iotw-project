package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.UserEntityContext;
import org.example.entity.UserVerifiable;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.*;
import org.example.entity.vo.response.AccountIdUsernameVO;
import org.example.entity.vo.response.AccountTableOV;
import org.example.mapper.AccountMapper;
import org.example.service.AccountService;
import org.example.utils.Const;
import org.example.utils.FlowUtils;
import org.example.utils.UserEntityUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 账户信息处理相关服务
 *
 * @author hwshou
 * @date 2025/5/19  22:11
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    //验证邮件发送冷却时间限制，秒为单位
    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    @Resource
    FlowUtils flowUtils;

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    AccountMapper accountMapper;

    /**
     * 从数据库中通过用户名或邮箱查找用户详细信息
     *
     * @param username 用户名
     * @return 用户详细信息
     * @throws UsernameNotFoundException 如果用户未找到则抛出此异常
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //传进来的username可能是username也可能是email
        Account account = this.findAccountByNameOrEmail(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(String.valueOf(account.getRole())) //数据库是Integer SpringSecurity是String
                .build();
    }

    /**
     * 邮件验证码重置密码操作，需要检查验证码是否正确
     *
     * @param vo 重置基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetEmailAccountPassword(AccountEmailRestVO vo) {
        String email = vo.getEmail();
        String verify = this.resetConfirm(new ConfirmResetVO(vo.getEmail(), vo.getCode()));
        if (verify != null) {
            return verify;
        }
        String password = passwordEncoder.encode(vo.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if (update) {
            stringRedisTemplate.delete(Const.VERIFY_EMAIL_DATA + email);
        }
        return null;
    }

    /**
     * 重置密码确认操作，验证验证码是否正确
     *
     * @param vo 验证基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String resetConfirm(ConfirmResetVO vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(Const.VERIFY_EMAIL_DATA + email);
        if (code == null) {
            return "请先获取验证码";
        }
        if (!code.equals(vo.getCode())) {
            return "验证码错误，请重新输入";
        }
        return null;
    }

    public Account findAccountByNameOrEmail(String text) {
        return this.query()
                .eq("username", text).or()
                .eq("email", text)
                .one();
    }

    /**
     * 生成注册验证码存入Redis中，并将邮件发送请求提交到消息队列等待发送
     *
     * @param type  类型
     * @param email 邮件地址
     * @param ip    请求IP地址
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) {    //防止同个ip同时多次访问
            if (!this.verifyLimit(ip)) {
                return "请求频繁, 请稍后再试";
            }
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend("mail", data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }

    }

    /**
     * 邮件验证码注册账号操作，需要检查验证码是否正确以及邮箱、用户名是否存在重名
     *
     * @param vo 注册基本信息
     * @return 操作结果，null表示正常，否则为错误原因
     */
    @Override
    public String registerEmailAccount(AccountEmailRegisterVO vo) {
        return UserEntityUtils.addUserEntity(
                vo,
                Account.class,
                new UserEntityContext<Account>() {

                    @Override
                    public boolean saveUser(Account entity) {
                        return accountMapper.insert(entity) > 0;
                    }

                    @Override
                    public boolean existsUserEmail(String email) {
                        return existsAccountEmail(email);
                    }

                    @Override
                    public boolean existsUsername(String username) {
                        return existsAccountUsername(username);
                    }
                },
                stringRedisTemplate,
                passwordEncoder,
                account -> {
                    account.setPassword(passwordEncoder.encode(vo.getPassword()));
                    account.setRole(Const.RULE_USER);
                }
        );
    }

    /**
     * 账户表单的多条件分页查询，支持排序
     *
     * @param vo 查询条件请求vo
     * @return 返回查询结果响应vo
     */
    @Override
    public IPage<AccountTableOV> queryByConditions(AccountQueryVO vo) {
        //1.构建分页对象
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Account> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                        vo.getPageNum(),
                        vo.getPageSize(),
                        true
                );
//        //设置优化参数
//        page.setOptimizeCountSql(false);

        //2.构建动态查询条件
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Account::getId, Account::getUsername, Account::getRole, Account::getBirth,
                        Account::getSex, Account::getEmail, Account::getAddress)
                .eq(Account::getDeleted, Const.IS_NOT_DELETED);

        //3. 条件组合
        if (vo.getId() != null) {
            wrapper.likeRight(Account::getId, vo.getId());
        }
        if (StringUtils.isNotBlank(vo.getUsername())) {
            wrapper.like(Account::getUsername, vo.getUsername());
        }
        if (vo.getRole() != null) {
            wrapper.eq(Account::getRole, vo.getRole());
        }
        if (vo.getSex() != null) {
            wrapper.eq(Account::getSex, vo.getSex());
        }
        if (vo.getStartBirth() != null && vo.getEndBirth() != null) {
            wrapper.between(Account::getBirth, vo.getStartBirth(), vo.getEndBirth());
        }
        if (StringUtils.isNotBlank(vo.getEmail())) {
            wrapper.like(Account::getEmail, vo.getEmail());
        }

        //4.动态排序
        wrapper.orderBy(true, vo.getSortAsc(),
                getSortLambda(vo.getSortField()));

        //5.执行分页查询
        IPage<Account> entityPage = accountMapper.selectPage(page, wrapper);

        //6.转化为VO分页
        return entityPage.convert(entity ->
                new AccountTableOV(
                        entity.getId(),
                        entity.getUsername(),
                        entity.getRole(),
                        entity.getBirth(),
                        entity.getSex(),
                        entity.getEmail(),
                        entity.getAddress()
                ));
    }

    /**
     * 逻辑删除账户记录
     *
     * @param id 需要删除账户的id
     * @return 是否删除成功信息
     */
    @Override
    public String logicDeleteOneAccountRecord(Integer id) {
        int deleted = accountMapper.deleteById(id);
        if (deleted != 0) {
            return null;
        }
        return "删除失败";
    }

    @Override
    @Transactional
    public String updateOneAccount(AccountUpdateVO vo) {
        //1.参数校验
        if (vo.getId() == null) {
            return "id不能为空";
        }
        if (StringUtils.isNotBlank(vo.getEmail()) && this.existsAccountEmail(vo.getEmail())) {
            return "此邮箱已被其他用户注册";
        }

        //2.查询现有账户
        Account account = accountMapper.selectById(vo.getId());
        if (account == null) {
            return "账户不存在";
        }

        //3. 安全转换
        Account updatedAccount = vo.asDTO(Account.class, target -> {
            target.setUpdateTime(LocalDateTime.now());  //特殊字段处理
        });

        //4.执行更新操作
        int result = accountMapper.updateById(updatedAccount);
        return result > 0 ? null : "数据未变化";
    }

    @Override
    public String addOneAccount(AccountAddVO vo) {
        return UserEntityUtils.addUserEntity(
                vo,
                Account.class,
                new UserEntityContext<Account>() {

                    @Override
                    public boolean saveUser(Account entity) {
                        return accountMapper.insert(entity) > 0;
                    }

                    @Override
                    public boolean existsUserEmail(String email) {
                        return existsAccountEmail(email);
                    }

                    @Override
                    public boolean existsUsername(String username) {
                        return existsUserEmail(username);
                    }
                },
                stringRedisTemplate,
                passwordEncoder,
                account -> {
                    account.setPassword(passwordEncoder.encode(vo.getPassword()));
                }
        );
    }

    /**
     * 获取account表中所有账户的id和username
     *
     * @return 列表结果
     */
    @Override
    public List<AccountIdUsernameVO> getAllAccountIdsAndUsernames() {
        // 构建查询条件：只查询未删除的账户，选择id和username字段
        LambdaQueryWrapper<Account> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Account::getId, Account::getUsername)
                .eq(Account::getDeleted, Const.IS_NOT_DELETED);

        // 执行查询并转换为VO列表
        return this.list(wrapper).stream()
                .map(account -> {
                    AccountIdUsernameVO vo = new AccountIdUsernameVO();
                    vo.setId(account.getId());
                    vo.setUsername(account.getUsername());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    private <T extends UserVerifiable> String verifyRegistration(T vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
        String key = Const.VERIFY_EMAIL_DATA + email;
        String code = stringRedisTemplate.opsForValue().get(key);
        if (code == null) {
            return "请先获取验证码";
        }
        if (!code.equals(vo.getCode())) {
            return "验证码输入错误，请重新输入";
        }
        if (this.existsAccountEmail(email)) {
            return "此邮箱已被其他用户注册";
        }
        if (this.existsAccountUsername(username)) {
            return "此用户名已被其他用户注册，请更新一个新的用户名";
        }
        return null;
    }

    private SFunction<Account, ?> getSortLambda(String field) {
        return switch (field) {
            case "birth" -> Account::getBirth;
            case "role" -> Account::getRole;
            case "register_time" -> Account::getRegisterTime;
            default -> Account::getId;
        };
    }

    private boolean existsAccountEmail(String email) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email", email));
    }

    private boolean existsAccountUsername(String username) {
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username", username));
    }

    /**
     * 针对IP地址进行邮件验证码获取限流
     *
     * @param ip 地址
     * @return 是否通过验证
     */
    private boolean verifyLimit(String ip) {
        String key = Const.VERIFY_EMAIL_LIMIT + ip;
        return flowUtils.limitOnceCheck(key, verifyLimit);
    }
}
