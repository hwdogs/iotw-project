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

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 顾客多条件分页查询，支持排序
     *
     * @param vo 查询条件请求vo
     * @return 返回查询结果响应vo
     */
    @Override
    public IPage<CustomerTableVO> queryCustomerTableByCondition(CustomerQueryVO vo) {
        return UserEntityUtils.queryByConditions(
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
                        Customer::getUpdateTime
                ),
                // 条件构造器
                (v, wrapper) -> UserEntityUtils.buildCommonConditions(
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

    /**
     * 逻辑删除顾客
     *
     * @param id 需要删除顾客的id
     * @return 是否删除成功
     */
    @Override
    public String logicDeleteOneCustomer(Integer id) {
        return removeById(id) ? null : "删除失败";
    }

    /**
     * 注册一名顾客
     *
     * @param vo 顾客信息
     * @return 是否注册成功
     */
    @Override
    public String registerOneCustomer(AccountEmailRegisterVO vo) {
        return registerOrAddOneCustomer(vo);
    }

    /**
     * 添加一名顾客
     *
     * @param vo 顾客信息
     * @return 是否添加成功
     */
    @Override
    public String addOneCustomer(CustomerAddVO vo) {
        return registerOrAddOneCustomer(vo);
    }

    /**
     * 更新顾客信息
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @Override
    public String updateOneCustomer(CustomerUpdateVO vo) {
        return UserEntityUtils.updateUserEntity(
                vo,
                Customer.class,
                new UserUpdateContext<Customer>() {
                    @Override
                    public <V> Function<V, Integer> getIdGetter() {
                        return v -> ((CustomerUpdateVO) v).getCustomerId();
                    }

                    @Override
                    public Customer getUserById(Integer id) {
                        return customerMapper.selectById(id);
                    }

                    @Override
                    public boolean existsUserEmailExcludingId(String email, Integer excludeId) {
                        return customerMapper.exists(Wrappers.<Customer>query()
                                .eq("email", email)
                                .ne("customer_id", excludeId)
                                .eq("deleted", Const.IS_NOT_DELETED));
                    }

                    @Override
                    public boolean updateUser(Customer entity) {
                        return customerMapper.updateById(entity) > 0;
                    }
                },
                CustomerUpdateVO::getEmail,
                null
        );
    }

    /**
     * 注册或者添加一名顾客
     *
     * @param vo 注册或者添加顾客的信息
     * @return 是否注册或者添加成功
     */
    private String registerOrAddOneCustomer(AccountEmailRegisterVO vo) {
        return UserEntityUtils.addUserEntity(
                vo,
                Customer.class,
                new UserEntityContext<Customer>() {
                    @Override
                    public boolean saveUser(Customer entity) {
                        return customerMapper.insert(entity) > 0;
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
                // 自定义逻辑，密码加密
                supplier -> supplier.setPassword(passwordEncoder.encode(vo.getPassword()))
        );
    }


    private boolean existsAccountEmail(String email) {
        return this.customerMapper.exists(Wrappers.<Customer>query().eq("email", email));
    }

    private boolean existsAccountUsername(String username) {
        return this.customerMapper.exists(Wrappers.<Customer>query().eq("username", username));
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
