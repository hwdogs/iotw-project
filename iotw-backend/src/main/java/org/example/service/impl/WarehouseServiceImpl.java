package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.entity.dto.Warehouse;
import org.example.entity.vo.request.WarehouseQueryVO;
import org.example.entity.vo.response.WarehouseTableVO;
import org.example.mapper.WarehouseMapper;
import org.example.service.WarehouseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Service
public class WarehouseServiceImpl extends ServiceImpl<WarehouseMapper, Warehouse> implements WarehouseService {

    @Resource
    private WarehouseMapper warehouseMapper;

    @Override
    public IPage<WarehouseTableVO> queryWarehouseTableByConditions(WarehouseQueryVO vo) {
        // 1.构建分页对象
        Page<Warehouse> page = new Page<>(
                vo.getPageNum(),
                vo.getPageSize(),
                true
        );

        // 2.构建动态查询条件
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Warehouse::getWarehouseId, Warehouse::getWarehouseName, Warehouse::getAccountId,
                Warehouse::getArea, Warehouse::getDescription, Warehouse::getUpdateTime);

        // 3.条件查询
        if (StringUtils.isNotBlank(vo.getWarehouseName())) {
            wrapper.like(Warehouse::getWarehouseName, vo.getWarehouseName());
        }
        if (vo.getAccountId() != null) {
            wrapper.eq(Warehouse::getAccountId, vo.getAccountId());
        }
        if (vo.getStartArea() != null && vo.getEndArea() != null) {
            wrapper.between(Warehouse::getArea, vo.getStartArea(), vo.getEndArea());
        }
        if (vo.getStartUpdateTime() != null && vo.getEndUpdateTime() != null) {
            wrapper.between(Warehouse::getUpdateTime, vo.getStartUpdateTime(), vo.getEndUpdateTime());
        }

        // 4. 动态排序
        wrapper.orderBy(true, vo.getSortAsc(),
                getSortLambda(vo.getSortField()));

        // 5.转化为vo分页
        Page<Warehouse> warehousePage = warehouseMapper.selectPage(page, wrapper);

        return warehousePage.convert(entity -> new WarehouseTableVO(
                entity.getAccountId(),
                entity.getWarehouseName(),
                entity.getAccountId(),
                entity.getArea(),
                entity.getDescription(),
                entity.getUpdateTime()
        ));
    }

    private SFunction<Warehouse, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "account_id" -> Warehouse::getAccountId;
            case "area" -> Warehouse::getArea;
            case "update_time" -> Warehouse::getUpdateTime;
            default -> Warehouse::getWarehouseId;
        };
    }
}
