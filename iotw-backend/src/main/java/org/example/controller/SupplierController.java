package org.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@RestController
@RequestMapping("/api/supplier")
public class SupplierController {
    /**
     * 添加供应商
     *
     * @param vo 添加供应商的信息
     * @return 是否添加成功
     */
    @PostMapping("/add")
    public RestBean<Void> addOneSupplier(@Valid @RequestBody SupplierAddVO vo) {
        return responseUtils.messageHandle(vo, supplierService::addOneSupplier);
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


}
