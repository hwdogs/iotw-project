package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.dto.Supply;
import org.example.entity.vo.request.SupplyAddVO;
import org.example.entity.vo.request.SupplyQueryVO;
import org.example.entity.vo.request.SupplyUpdateVO;
import org.example.entity.vo.response.SupplyTableVO;
import org.example.mapper.GoodMapper;
import org.example.mapper.SupplierMapper;
import org.example.mapper.SupplyMapper;
import org.example.service.SupplyService;
import org.example.utils.Const;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * supply入库服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Service
public class SupplyServiceImpl extends ServiceImpl<SupplyMapper, Supply> implements SupplyService {

    @Resource
    private SupplierMapper supplierMapper;

    @Resource
    private GoodMapper goodMapper;

    /**
     * 请求入库记录列表
     *
     * @param vo 请求信息
     * @return 请求结果
     */
    @Override
    public IPage<SupplyTableVO> querySupplyTableByCondition(SupplyQueryVO vo) {
        // 1.构建分页对象
        Page<Supply> page = new Page<>(
                vo.getPageNum(),
                vo.getPageSize(),
                true);

        // 2.构建动态查询条件
        LambdaQueryWrapper<Supply> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Supply::getSupplyId, Supply::getSupplierId, Supply::getGoodId, Supply::getSupplyNumber,
                Supply::getStatus, Supply::getCreateTime, Supply::getUpdateTime);

        // 3. 条件组合
        if (vo.getSupplierId() != null) {
            wrapper.likeRight(Supply::getSupplierId, vo.getSupplierId());
        }
        if (vo.getGoodId() != null) {
            wrapper.likeRight(Supply::getGoodId, vo.getGoodId());
        }
        if (vo.getSupplierId() != null) {
            wrapper.likeRight(Supply::getSupplierId, vo.getSupplierId());
        }
        if (vo.getStatus() != null) {
            wrapper.eq(Supply::getStatus, vo.getStatus());
        }
        // 货物数量
        // [100, ...)
        if (vo.getStartSupplyNumber() != null && vo.getEndSupplyNumber() == null) {
            wrapper.ge(Supply::getSupplyNumber, vo.getStartSupplyNumber());
        }
        // (..., 200]
        if (vo.getStartSupplyNumber() == null && vo.getEndSupplyNumber() != null) {
            wrapper.le(Supply::getSupplyNumber, vo.getEndSupplyNumber());
        }
        // [100, 200]
        if (vo.getStartSupplyNumber() != null && vo.getEndSupplyNumber() != null) {
            wrapper.between(Supply::getSupplyNumber, vo.getStartSupplyNumber(), vo.getEndSupplyNumber());
        }
        // 创建时间
        if (vo.getStartCreateTime() != null && vo.getEndCreateTime() == null) {
            wrapper.ge(Supply::getCreateTime, vo.getStartCreateTime());
        }
        if (vo.getStartCreateTime() == null && vo.getEndCreateTime() != null) {
            wrapper.le(Supply::getCreateTime, vo.getEndCreateTime());
        }
        if (vo.getStartCreateTime() != null && vo.getEndCreateTime() != null) {
            wrapper.between(Supply::getCreateTime, vo.getStartCreateTime(), vo.getEndCreateTime());
        }
        // 更新时间
        if (vo.getStartUpdateTime() != null && vo.getEndUpdateTime() == null) {
            wrapper.ge(Supply::getUpdateTime, vo.getStartUpdateTime());
        }
        if (vo.getStartUpdateTime() == null && vo.getEndUpdateTime() != null) {
            wrapper.le(Supply::getUpdateTime, vo.getEndUpdateTime());
        }
        if (vo.getStartUpdateTime() != null && vo.getEndUpdateTime() != null) {
            wrapper.between(Supply::getCreateTime, vo.getStartUpdateTime(), vo.getEndUpdateTime());
        }

        // 4.动态排序
        wrapper.orderBy(true, vo.getSortAsc(),
                getSortLambda(vo.getSortField()));

        // 5.转化为VO分页
        Page<Supply> supplyPage = this.page(page, wrapper);

        return supplyPage.convert(entity -> new SupplyTableVO(
                entity.getSupplyId(),
                entity.getSupplierId(),
                entity.getGoodId(),
                entity.getSupplyNumber(),
                entity.getStatus(),
                entity.getCreateTime(),
                entity.getUpdateTime()
        ));
    }

    /**
     * 添加一条入库记录
     *
     * @param vo 进货信息
     * @return 是否入库成功
     */
    @Override
    public String addOneSupply(SupplyAddVO vo) {
        // 1. 参数校验
        if (vo.getSupplierId() == null) {
            return "供货商ID不能为空";
        }
        if (vo.getGoodId() == null) {
            return "货物ID不能为空";
        }

        /*
            不需要检查数据库中是否已存在将要添加的记录，
            同一个供货商可以进同一个货物不同时多次
         */

        // 2。 外键校验
        if (supplierMapper.selectById(vo.getSupplierId()) == null) {
            return "供货商不存在";
        }
        if (goodMapper.selectById(vo.getGoodId()) == null) {
            return "货物不存在";
        }

        // 3. 安全转换
        Supply supply = vo.asDTO(Supply.class, target -> {
            target.setStatus(Const.NOT_APPROVED);
            target.setCreateTime(LocalDateTime.now());
            target.setUpdateTime(LocalDateTime.now());
        });

        return this.save(supply) ? null : "添加失败";
    }

    /**
     * 更新一条入库信息
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @Override
    public String updateOneSupply(SupplyUpdateVO vo) {
        // 1.参数校验
        if (vo.getSupplyId() == null) {
            return "入库ID不能为空";
        }
        if (vo.getSupplierId() == null && vo.getGoodId() == null) {
            return "供货商和货物ID不能同时为空";
        }

        // 2.1 查询现有入库记录
        LambdaQueryWrapper<Supply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Supply::getSupplyId, vo.getSupplyId());
        if (this.count(queryWrapper) == 0) {
            return "更新的入库记录不存在";
        }

        // 2.2 如果记录存在则检查新信息是否存在
        if (vo.getSupplierId() != null && supplierMapper.selectById(vo.getSupplierId()) == null) {
            return "新供货商不存在";
        }
        if (vo.getGoodId() != null && goodMapper.selectById(vo.getGoodId()) == null) {
            return "新货物不存在";
        }

        // 3. 安全转换，只处理需要特殊处理的字段
        Supply supply = vo.asDTO(Supply.class, target -> {
            target.setUpdateTime(LocalDateTime.now());
            if (vo.getStatus() != null) {
                target.setStatus(vo.getStatus());
            }
        });

        return this.updateById(supply) ? null : "数据未变化";
    }


    private SFunction<Supply, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "supply_id" -> Supply::getSupplyId;
            case "supply_number" -> Supply::getSupplyNumber;
            case "supplier_id" -> Supply::getSupplierId;
            case "good_id" -> Supply::getGoodId;
            case "create_time" -> Supply::getCreateTime;
            default -> Supply::getUpdateTime;
        };
    }
}
