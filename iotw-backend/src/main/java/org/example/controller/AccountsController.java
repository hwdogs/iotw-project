package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
import org.example.entity.vo.request.AccountAddVO;
import org.example.entity.vo.request.AccountQueryVO;
import org.example.entity.vo.request.AccountUpdateVO;
import org.example.entity.vo.response.AccountIdUsernameVO;
import org.example.entity.vo.response.AccountTableOV;
import org.example.service.AccountService;
import org.example.utils.ResponseUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 处理总体账户操作
 *
 * @author hwshou
 * @date 2025/5/27  12:55
 */
@RestController
@RequestMapping("/api/account")
public class AccountsController {

    @Resource
    private AccountService accountService;

    @Resource
    private ResponseUtils responseUtils;

    /**
     * 请求账户信息
     *
     * @param vo 请求信息
     * @return 响应对应vo类
     */
    @PostMapping("/query")
    public RestBean<IPage<AccountTableOV>> queryAccounts(
            @Valid @RequestBody AccountQueryVO vo) {
        return RestBean.success(accountService.queryByConditions(vo));
    }

    /**
     * 逻辑删除账户
     *
     * @param id 需要逻辑删除的账户id
     * @return 是否删除成功
     */
    @GetMapping("/delete")
    public RestBean<Void> deleteOneAccount(@RequestParam Integer id) {
        return responseUtils.messageHandle(id, accountService::logicDeleteOneAccountRecord);
    }

    /**
     * 更新账户信息
     *
     * @param vo 需要更新的账户信息vo类
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public RestBean<Void> updateOneAccount(@Valid @RequestBody AccountUpdateVO vo) {
        return responseUtils.messageHandle(vo, accountService::updateOneAccount);
    }

    /**
     * 需要认证后的注册接口。进行用户注册操作，需要先请求邮件验证码
     *
     * @param vo 注册信息
     * @return 是否注册成功
     */
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody AccountAddVO vo) {
        return responseUtils.messageHandle(vo, accountService::addOneAccount);
    }

    /**
     * 请求邮件验证码
     *
     * @param email   请求邮件
     * @param type    类型
     * @param request 请求
     * @return 是否请求成功
     */
    @GetMapping("/ask-code")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp = "(register|reset)") String type,
                                        HttpServletRequest request) {
        return responseUtils.messageHandle(() ->
                accountService.registerEmailVerifyCode(type, email, request.getRemoteAddr()));
    }

    /**
     * 请求所有账户id和username
     *
     * @return 请求结果
     */
    @GetMapping("/ids-and-usernames")
    public RestBean<List<AccountIdUsernameVO>> getAccountIdsAndUsernames(){
        return RestBean.success(accountService.getAllAccountIdsAndUsernames());
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/info")
    public RestBean<AccountTableOV> getCurrentAccountInfo() {
        return RestBean.success(accountService.getCurrentAccountInfo());
    }
}
