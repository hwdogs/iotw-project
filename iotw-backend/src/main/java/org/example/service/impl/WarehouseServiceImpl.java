package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.entity.dto.Manage;
import org.example.entity.dto.Warehouse;
import org.example.entity.vo.request.WarehouseQueryVO;
import org.example.entity.vo.response.WarehouseTableVO;
import org.example.mapper.ManageMapper;
import org.example.mapper.WarehouseMapper;
import org.example.service.WarehouseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Resource
    private ManageMapper manageMapper;

    /**
     * 多条件分页查询仓库，支持联合manage表查询
     *
     * @param vo 查询仓库vo类
     * @return 查询到的分页信息
     */
    @Override
    public IPage<WarehouseTableVO> queryWarehouseTableByConditions(WarehouseQueryVO vo) {
        // 1. 构建分页对象
        // 参数说明：当前页码、每页数量、是否进行count查询（true表示查询总记录数）
        Page<Warehouse> page = new Page<>(
                vo.getPageNum(),
                vo.getPageSize(),
                true
        );

        // 2. 构建动态查询条件
        // 使用LambdaQueryWrapper确保类型安全，避免字段名硬编码
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        // 指定查询字段（避免SELECT * 性能问题）
        wrapper.select(Warehouse::getWarehouseId, Warehouse::getWarehouseName,
                Warehouse::getArea, Warehouse::getDescription, Warehouse::getUpdateTime);

        // 3. 动态条件组装
        // 仓库名称模糊查询
        if (StringUtils.isNotBlank(vo.getWarehouseName())) {
            wrapper.like(Warehouse::getWarehouseName, vo.getWarehouseName());
        }
        // 面积范围查询
        if (vo.getStartArea() != null && vo.getEndArea() != null) {
            wrapper.between(Warehouse::getArea, vo.getStartArea(), vo.getEndArea());
        }
        // 更新时间范围查询
        if (vo.getStartUpdateTime() != null && vo.getEndUpdateTime() != null) {
            wrapper.between(Warehouse::getUpdateTime, vo.getStartUpdateTime(), vo.getEndUpdateTime());
        }

        // 4. 动态排序处理
        // 根据前端传入的排序字段和方向进行排序
        wrapper.orderBy(true, vo.getSortAsc(),
                getSortLambda(vo.getSortField()));

        // 5. 执行分页查询
        // 获取基础仓库数据（不包含关联的accountIds）
        Page<Warehouse> warehousePage = warehouseMapper.selectPage(page, wrapper);

        // 6. 批量加载关联账户ID（性能关键点）
        // 使用内存分组避免N+1查询问题
        Map<Integer, List<Integer>> accountMap = loadAccountIds(warehousePage.getRecords());

        // 7. 转换实体为VO对象
        // 保持原始分页信息（total/size/current等），仅替换records内容
        return warehousePage.convert(entity -> {
            WarehouseTableVO voItem = new WarehouseTableVO(
                    entity.getWarehouseId(),
                    entity.getWarehouseName(),
                    null,  // accountIds单独设置
                    entity.getArea(),
                    entity.getDescription(),
                    entity.getUpdateTime()
            );
            // 设置关联账户ID列表
            // 使用getOrDefault确保无账户时返回空列表而非null
            voItem.setAccountIds(accountMap.getOrDefault(entity.getWarehouseId(), Collections.emptyList()));
            return voItem;
        });
    }

    /**
     * 批量加载仓库关联的账户ID
     * <p>
     * 说明：
     * 1. 单次批量查询替代循环多次查询（解决N+1问题）
     * 2. 内存分组处理减少数据库压力
     *
     * @param warehouses 仓库实体列表
     * @return Map结构：key=仓库ID, value=关联账户ID列表
     */
    private Map<Integer, List<Integer>> loadAccountIds(List<Warehouse> warehouses) {
        // 空集合快速返回
        if (warehouses.isEmpty()) return Collections.emptyMap();

        // 提取仓库ID列表（用于IN查询）
        List<Integer> warehouseIds = warehouses.stream()
                .map(Warehouse::getWarehouseId)
                .collect(Collectors.toList());

        // 执行批量查询：获取仓库-账户关系
        // 实际SQL：SELECT warehouse_id, account_id FROM t_manage WHERE warehouse_id IN (?,?,...)
        List<Manage> manages = manageMapper.selectByWarehouseIds(warehouseIds);

        // 按仓库ID分组账户ID
        // 数据结构转换：List<Manage> -> Map<仓库ID, 账户ID列表>
        return manages.stream()
                .collect(Collectors.groupingBy(
                        Manage::getWarehouseId,     // 分组键：仓库ID
                        Collectors.mapping(         // 值转换：提取accountId并转为List
                                Manage::getAccountId,
                                Collectors.toList())
                ));
    }

    /**
     * 逻辑删除仓库记录
     *
     * @param warehouseId 需要删除的仓库id
     * @return 是否删除成功
     */
    @Override
    public String logicDeleteOneWarehouseRecord(Integer warehouseId) {
        int deleted = warehouseMapper.deleteById(warehouseId);
        if (deleted != 0) {
            return null;
        }
        return "删除失败";
    }

    @Override
    public String updateOneAccount() {
        return "";
    }

    private SFunction<Warehouse, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "area" -> Warehouse::getArea;
            case "update_time" -> Warehouse::getUpdateTime;
            default -> Warehouse::getWarehouseId;
        };
    }
}
