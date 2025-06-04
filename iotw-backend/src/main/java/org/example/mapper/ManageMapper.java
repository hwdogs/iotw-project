package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.entity.dto.Manage;

import java.util.List;

/**
 * <p>
 * manage Mapper 接口
 * </p>
 *
 * @author hwshou
 * @since 2025-05-31 14:51
 */
@Mapper
public interface ManageMapper extends BaseMapper<Manage> {

    List<Manage> selectByWarehouseIds(@Param("warehouseIds") List<Integer> warehouseIds);

    List<Integer> selectAccountIdsByWarehouseId(@Param("warehouseId") Integer warehouseId);
}

