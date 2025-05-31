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

    @Select("<script>" +
            "SELECT warehouse_id, account_id FROM t_manage " +
            "WHERE warehouse_id IN " +
            "<foreach item='id' collection='warehouseIds' open='(' separator=',' close=')'>" +
            "   #{id}" +
            "</foreach>" +
            "</script>")
//    @Results({
//            @Result(column = "warehouse_id", property = "warehouseId"),
//            @Result(column = "account_id", property = "accountId")
//    })
    List<Manage> selectByWarehouseIds(@Param("warehouseIds") List<Integer> warehouseIds);
}

