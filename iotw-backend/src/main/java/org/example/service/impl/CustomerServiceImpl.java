package org.example.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.UserEntityContext;
import org.example.entity.UserUpdateContext;
import org.example.entity.dto.Customer;
import org.example.entity.vo.request.AccountEmailRegisterVO;
import org.example.entity.vo.request.CustomerAddVO;
import org.example.entity.vo.request.CustomerQueryVO;
import org.example.entity.vo.request.CustomerUpdateVO;
import org.example.entity.vo.response.CustomerTableVO;
import org.example.mapper.CustomerMapper;
import org.example.service.CustomerService;
import org.example.utils.Const;
import org.example.utils.PageQueryUtils;
import org.example.utils.UserEntityUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * <p>
 * customer服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {


    /**
     * 顾客多条件分页查询，支持排序
     *
     * @param vo 查询条件请求vo
     * @return 返回查询结果响应vo
     */
    @Override
    public IPage<CustomerTableVO> queryCustomerTableByCondition(CustomerQueryVO vo) {
        return PageQueryUtils.queryByConditions(
                vo,
                // 分页参数获取函数
                v -> v.getPageNum().longValue(),
                v -> v.getPageSize().longValue(),
                //服务实例
                this,
                //字段选择器
                wrapper -> wrapper.select(
                        Customer::getCustomerId,
                        Customer::getUsername,
                        Customer::getEmail,
                        Customer::getPhone,
                        Customer::getAddress,
                        Customer::getSex,
                        Customer::getBirth,
                        Customer::getRegisterTime,
                        Customer::getUsername
                ),
                // 条件构造器
                (v, wrapper) -> PageQueryUtils.buildCommonConditions(
                        v,
                        wrapper,
                        // 值获取函数
                        CustomerQueryVO::getCustomerId,
                        CustomerQueryVO::getUsername,
                        CustomerQueryVO::getEmail,
                        CustomerQueryVO::getPhone,
                        CustomerQueryVO::getAddress,
                        CustomerQueryVO::getSex,
                        CustomerQueryVO::getStartBirth,
                        CustomerQueryVO::getEndBirth,
                        CustomerQueryVO::getStartRegisterTime,
                        CustomerQueryVO::getEndRegisterTime,
                        CustomerQueryVO::getStartUpdateTime,
                        CustomerQueryVO::getEndUpdateTime,
                        // 字段获取函数
                        Customer::getCustomerId,
                        Customer::getUsername,
                        Customer::getEmail,
                        Customer::getPhone,
                        Customer::getAddress,
                        Customer::getSex,
                        Customer::getBirth,
                        Customer::getRegisterTime,
                        Customer::getUpdateTime,
                        // 排序相关
                        CustomerQueryVO::getSortField,
                        CustomerQueryVO::getSortAsc,
                        this::getSortLambda
                ),
                // 实体转换器
                entity -> new CustomerTableVO(
                        entity.getCustomerId(),
                        entity.getUsername(),
                        entity.getEmail(),
                        entity.getPhone(),
                        entity.getAddress(),
                        entity.getSex(),
                        entity.getBirth(),
                        entity.getRegisterTime(),
                        entity.getUpdateTime()
                )
        );
    }


    private SFunction<Customer, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "customer_id" -> Customer::getCustomerId;
            case "birth" -> Customer::getBirth;
            case "register_time" -> Customer::getRegisterTime;
            default -> Customer::getUpdateTime;
        };
    }
}
