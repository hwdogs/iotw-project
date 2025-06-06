package org.example.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.UserEntityContext;
import org.example.entity.UserUpdateContext;
import org.example.entity.dto.Supplier;
import org.example.entity.vo.request.AccountEmailRegisterVO;
import org.example.entity.vo.request.SupplierAddVO;
import org.example.entity.vo.request.SupplierQueryVO;
import org.example.entity.vo.request.SupplierUpdateVO;
import org.example.entity.vo.response.SupplierTableVO;
import org.example.mapper.SupplierMapper;
import org.example.service.SupplierService;
import org.example.utils.Const;
import org.example.utils.UserEntityUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * <p>
 * supplier服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Resource
    private SupplierMapper supplierMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 供应商多条件分页查询查询，支持排序
     *
     * @param vo 查询条件请求vo
     * @return 返回查询结果响应vo
     */
    @Override
    public IPage<SupplierTableVO> querySupplierTableByCondition(SupplierQueryVO vo) {
        return UserEntityUtils.queryByConditions(
                vo,
                // 分页参数获取函数
                v -> v.getPageNum().longValue(),
                v -> v.getPageSize().longValue(),
                //服务实例
                this,
                //字段选择器
                wrapper -> wrapper.select(
                        Supplier::getSupplierId,
                        Supplier::getUsername,
                        Supplier::getEmail,
                        Supplier::getPhone,
                        Supplier::getAddress,
                        Supplier::getSex,
                        Supplier::getBirth,
                        Supplier::getRegisterTime,
                        Supplier::getUpdateTime
                ),
                // 条件构造器
                (v, wrapper) -> UserEntityUtils.buildCommonConditions(
                        v,
                        wrapper,
                        // 值获取函数
                        SupplierQueryVO::getSupplierId,
                        SupplierQueryVO::getUsername,
                        SupplierQueryVO::getEmail,
                        SupplierQueryVO::getPhone,
                        SupplierQueryVO::getAddress,
                        SupplierQueryVO::getSex,
                        SupplierQueryVO::getStartBirth,
                        SupplierQueryVO::getEndBirth,
                        SupplierQueryVO::getStartRegisterTime,
                        SupplierQueryVO::getEndRegisterTime,
                        SupplierQueryVO::getStartUpdateTime,
                        SupplierQueryVO::getEndUpdateTime,
                        // 字段获取函数
                        Supplier::getSupplierId,
                        Supplier::getUsername,
                        Supplier::getEmail,
                        Supplier::getPhone,
                        Supplier::getAddress,
                        Supplier::getSex,
                        Supplier::getBirth,
                        Supplier::getRegisterTime,
                        Supplier::getUpdateTime,
                        // 排序相关
                        SupplierQueryVO::getSortField,
                        SupplierQueryVO::getSortAsc,
                        this::getSortLambda
                ),
                // 实体转换器
                entity -> new SupplierTableVO(
                        entity.getSupplierId(),
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

        /*
        // 1.构建分页对象
        Page<Supplier> page = new Page<>(
                vo.getPageNum(),
                vo.getPageSize(),
                true
        );

        // 2.构建动态查询条件
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Supplier::getSupplierId, Supplier::getUsername, Supplier::getEmail, Supplier::getPhone,
                Supplier::getAddress, Supplier::getSex, Supplier::getBirth, Supplier::getRegisterTime, Supplier::getUpdateTime);

        // 3. 条件组合
        if (vo.getSupplierId() != null) {
            wrapper.likeRight(Supplier::getSupplierId, vo.getSupplierId());
        }
        if (StringUtils.isNotBlank(vo.getUsername())) {
            wrapper.like(Supplier::getUsername, vo.getUsername());
        }
        if (StringUtils.isNotBlank(vo.getEmail())) {
            wrapper.eq(Supplier::getEmail, vo.getEmail());
        }
        if (StringUtils.isNotBlank(vo.getPhone())) {
            wrapper.likeRight(Supplier::getPhone, vo.getPhone());
        }
        if (StringUtils.isNotBlank(vo.getAddress())) {
            wrapper.like(Supplier::getAddress, vo.getAddress());
        }
        if (vo.getSex() != null) {
            wrapper.eq(Supplier::getSex, vo.getSex());
        }
        if (vo.getStartBirth() != null && vo.getEndBirth() != null &&
                StringUtils.isNotBlank(vo.getStartBirth().toString()) &&
                StringUtils.isNotBlank(vo.getEndBirth().toString())) {
            wrapper.between(Supplier::getBirth, vo.getStartBirth(), vo.getEndBirth());
        }
        if (vo.getStartRegisterTime() != null && vo.getEndUpdateTime() != null &&
                StringUtils.isNotBlank(vo.getStartRegisterTime().toString()) &&
                StringUtils.isNotBlank(vo.getEndUpdateTime().toString())) {
            wrapper.between(Supplier::getRegisterTime, vo.getStartRegisterTime(), vo.getEndRegisterTime());
        }
        if (vo.getStartUpdateTime() != null && vo.getEndUpdateTime() != null &&
                StringUtils.isNotBlank(vo.getStartUpdateTime().toString()) &&
                StringUtils.isNotBlank(vo.getEndUpdateTime().toString())) {
            wrapper.between(Supplier::getUpdateTime, vo.getStartUpdateTime(), vo.getEndUpdateTime());
        }

        // 4.动态排序
        wrapper.orderBy(true, vo.getSortAsc(),
                getSortLambda(vo.getSortField()));

        // 5.转化为VO分页
        Page<Supplier> supplierPage = this.page(page, wrapper);

        return supplierPage.convert(entity -> new SupplierTableVO(
                entity.getSupplierId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getSex(),
                entity.getBirth(),
                entity.getRegisterTime(),
                entity.getUpdateTime()
        ));*/
    }

    /**
     * 逻辑删除供应商
     *
     * @param id 需要删除供应商的id
     * @return 是否删除成功
     */
    @Override
    public String logicDeleteOneSupplier(Integer id) {
        return removeById(id) ? null : "删除失败";
    }

    /**
     * 注册一名供应商
     *
     * @param vo 供应商信息
     * @return 是否注册成功
     */
    @Override
    public String registerOneSupplier(AccountEmailRegisterVO vo) {
        return registerOrAddOneSupplier(vo);
    }

    /**
     * 添加一名供应商
     *
     * @param vo 供应商信息
     * @return 是否添加成功
     */
    @Override
    public String addOneSupplier(SupplierAddVO vo) {
        return registerOrAddOneSupplier(vo);
    }

    /**
     * 更新供应商信息
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @Override
    public String updateOneSupplier(SupplierUpdateVO vo) {
        return UserEntityUtils.updateUserEntity(
                vo,
                Supplier.class,
                new UserUpdateContext<Supplier>() {
                    @Override
                    public <V> Function<V, Integer> getIdGetter() {
                        return v -> ((SupplierUpdateVO) v).getSupplierId();
                    }

                    @Override
                    public Supplier getUserById(Integer id) {
                        return supplierMapper.selectById(id);
                    }

                    @Override
                    public boolean existsUserEmailExcludingId(String email, Integer excludeId) {
                        return supplierMapper.exists(Wrappers.<Supplier>query()
                                .eq("email", email)
                                .ne("supplier_id", excludeId)
                                .eq("deleted", Const.IS_NOT_DELETED));
                    }

                    @Override
                    public boolean updateUser(Supplier entity) {
                        return supplierMapper.updateById(entity) > 0;
                    }
                },
                SupplierUpdateVO::getEmail,
                null
        );
    }

    /**
     * 注册或者添加一名供应商
     *
     * @param vo 注册或者添加供应商的信息
     * @return 是否注册或者添加成功
     */
    private String registerOrAddOneSupplier(AccountEmailRegisterVO vo) {
        return UserEntityUtils.addUserEntity(
                vo, //实现UserVerifiable
                Supplier.class, //继承BaseUserEntity
                new UserEntityContext<Supplier>() {
                    @Override
                    public boolean saveUser(Supplier entity) {
                        return supplierMapper.insert(entity) > 0;
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
                // 自定义逻辑：密码加密
                supplier -> supplier.setPassword(passwordEncoder.encode(supplier.getPassword()))
        );
    }


    private boolean existsAccountEmail(String email) {
        return this.supplierMapper.exists(Wrappers.<Supplier>query().eq("email", email));
    }

    private boolean existsAccountUsername(String username) {
        return this.supplierMapper.exists(Wrappers.<Supplier>query().eq("username", username));
    }


    private SFunction<Supplier, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "supplier_id" -> Supplier::getSupplierId;
            case "birth" -> Supplier::getBirth;
            case "register_time" -> Supplier::getRegisterTime;
            default -> Supplier::getUpdateTime;
        };
    }
}
