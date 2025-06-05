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
    @Resource
    private SupplierService supplierService;

    @Resource
    private ResponseUtils responseUtils;

    @Resource
    private AccountService accountService;

    /**
     * 请求供应商信息列表
     *
     * @param vo 请求信息
     * @return 响应回应vo类
     */
    @PostMapping("/query")
    public RestBean<IPage<SupplierTableVO>> querySupplierTableByCondition(
            @Valid @RequestBody SupplierQueryVO vo) {
        return RestBean.success(supplierService.querySupplierTableByCondition(vo));
    }

    /**
     * 逻辑删除供应商
     *
     * @param id 需要删除的供应商id
     * @return 是否删除失败
     */
    @GetMapping("/delete")
    public RestBean<Void> logicDeleteOneSupplier(@RequestParam Integer id) {
        return responseUtils.messageHandle(id, supplierService::logicDeleteOneSupplier);
    }

    /**
     * 注册供应商
     *
     * @param vo 注册信息
     * @return 是否注册成功
     */
    @PostMapping("/register")
    public RestBean<Void> registerOneSupplier(@Valid @RequestBody AccountEmailRegisterVO vo) {
        return responseUtils.messageHandle(vo, supplierService::registerOneSupplier);
    }

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

    /**
     * 更新一名供应商信息
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public RestBean<Void> updateOneSupplier(@Valid @RequestBody SupplierUpdateVO vo) {
        return responseUtils.messageHandle(vo, supplierService::updateOneSupplier);
    }

}
