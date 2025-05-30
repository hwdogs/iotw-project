package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
import org.example.entity.vo.request.ConfirmResetVO;
import org.example.entity.vo.request.EmailRegisterVO;
import org.example.entity.vo.request.EmailRestVO;
import org.example.service.AccountService;
import org.example.utils.ResponseUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 用于验证相关Controller包含用户的注册、重置密码等操作
 *
 * @author hwshou
 * @date 2025/5/24  22:39
 */
@Validated
@RestController()
@RequestMapping("/api/auth")
public class AuthorizeController {

    @Resource
    AccountService service;

    @Resource
    private ResponseUtils responseUtils;

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
                service.registerEmailVerifyCode(type, email, request.getRemoteAddr()));
    }

    /**
     * 进行用户注册操作，需要先请求邮件验证码
     *
     * @param vo 注册信息
     * @return 是否注册成功
     */
    @PostMapping("/register")
    public RestBean<Void> register(@RequestBody EmailRegisterVO vo) {
        return responseUtils.messageHandle(vo, service::registerEmailAccount);
    }

    /**
     * 执行密码重置确认，检查验证码是否正确
     *
     * @param vo 密码重置信息
     * @return 是否操作成功
     */
    @PostMapping("/reset-confirm")
    public RestBean<Void> resetConfirm(@RequestBody ConfirmResetVO vo) {
        return responseUtils.messageHandle(vo, service::resetConfirm);
    }

    /**
     * 执行密码重置操作
     *
     * @param vo 密码重置信息
     * @return 是否操作成功
     */
    @PostMapping("/reset-password")
    public RestBean<Void> resetPassword(@RequestBody EmailRestVO vo) {
        return responseUtils.messageHandle(vo, service::resetEmailAccountPassword);
    }


}
