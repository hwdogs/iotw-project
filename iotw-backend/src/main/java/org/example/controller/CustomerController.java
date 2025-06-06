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

}
