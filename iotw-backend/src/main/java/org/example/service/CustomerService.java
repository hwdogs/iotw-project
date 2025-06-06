package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Customer;
import org.example.entity.vo.request.AccountEmailRegisterVO;
import org.example.entity.vo.request.CustomerAddVO;
import org.example.entity.vo.request.CustomerQueryVO;
import org.example.entity.vo.request.CustomerUpdateVO;
import org.example.entity.vo.response.CustomerTableVO;

/**
 * <p>
 * customer服务类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
public interface CustomerService extends IService<Customer> {
    IPage<CustomerTableVO> queryCustomerTableByCondition(CustomerQueryVO vo);

    String logicDeleteOneCustomer(Integer id);

}
