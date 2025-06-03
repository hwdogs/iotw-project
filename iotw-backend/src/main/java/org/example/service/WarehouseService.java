package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.entity.dto.Warehouse;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.vo.request.WarehouseAddVO;
import org.example.entity.vo.request.WarehouseQueryVO;
import org.example.entity.vo.request.WarehouseUpdateVO;
import org.example.entity.vo.response.WarehouseTableVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
public interface WarehouseService extends IService<Warehouse> {
    IPage<WarehouseTableVO> queryWarehouseTableByConditions(WarehouseQueryVO vo);

    String logicDeleteOneWarehouseRecord(Integer warehouseId);

    String updateOneWarehouse(WarehouseUpdateVO vo);

    String addOneWarehouse(WarehouseAddVO vo);
}
