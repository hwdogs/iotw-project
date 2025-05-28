package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.AccountQueryVO;
import org.example.entity.vo.request.ConfirmResetVO;
import org.example.entity.vo.request.EmailRegisterVO;
import org.example.entity.vo.request.EmailRestVO;
import org.example.entity.vo.response.AccountTableOV;
import org.example.mapper.AccountMapper;
import org.example.service.AccountService;
import org.example.utils.Const;
import org.example.utils.FlowUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    @Autowired
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
    public String resetEmailAccountPassword(EmailRestVO vo) {
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
    public String registerEmailAccount(EmailRegisterVO vo) {
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
        String password = passwordEncoder.encode(vo.getPassword());
        Account account = new Account(null,
                username,
                password,
                email,
                Const.RULE_USER,
                LocalDateTime.now(),
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                Const.IS_NOT_DELETED
        );
        if (this.save(account)) {
            stringRedisTemplate.delete(key);
            return null;
        } else {
            return "内部错误，请联系管理员";
        }
    }


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
                        Account::getSex, Account::getEmail, Account::getAddress, Account::getEmail)
                .eq(Account::getDeleted, Const.IS_NOT_DELETED);

        //3. 条件组合
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
