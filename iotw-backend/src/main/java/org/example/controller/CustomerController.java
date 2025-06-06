package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.example.entity.RestBean;
import org.example.entity.vo.request.AccountEmailRegisterVO;
import org.example.entity.vo.request.CustomerAddVO;
import org.example.entity.vo.request.CustomerQueryVO;
import org.example.entity.vo.request.CustomerUpdateVO;
import org.example.entity.vo.response.CustomerTableVO;
import org.example.service.AccountService;
import org.example.service.CustomerService;
import org.example.utils.ResponseUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * customer前端控制器
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Resource
    private CustomerService customerService;

    @Resource
    private ResponseUtils responseUtils;

    @Resource
    private AccountService accountService;

    /**
     * 请求顾客类别
     *
     * @param vo 请求信息
     * @return 请求结果
     */
    @PostMapping("/query")
    public RestBean<IPage<CustomerTableVO>> queryCustomerTableByCondition(
            @Valid @RequestBody CustomerQueryVO vo) {
        return RestBean.success(customerService.queryCustomerTableByCondition(vo));
    }

    /**
     * 逻辑删除顾客
     *
     * @param id 需要删除的顾客id
     * @return 是否删除失败
     */
    @GetMapping("/delete")
    public RestBean<Void> logicDeleteOneCustomer(Integer id) {
        return responseUtils.messageHandle(id, customerService::logicDeleteOneCustomer);
    }

    /**
     * 注册顾客
     *
     * @param vo 注册信息
     * @return 是否注册成功
     */
    @PostMapping("/register")
    public RestBean<Void> registerOneCustomer(@Valid @RequestBody AccountEmailRegisterVO vo) {
        return responseUtils.messageHandle(vo, customerService::registerOneCustomer);
    }

    /**
     * 添加顾客
     *
     * @param vo 添加顾客的信息
     * @return 是否添加成功
     */
    @PostMapping("/add")
    public RestBean<Void> addOneCustomer(@Valid @RequestBody CustomerAddVO vo) {
        return responseUtils.messageHandle(vo, customerService::addOneCustomer);
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
     * 更新一名顾客信息
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public RestBean<Void> updateOneCustomer(@Valid @RequestBody CustomerUpdateVO vo) {
        return responseUtils.messageHandle(vo, customerService::updateOneCustomer);
    }
}
