package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.example.entity.BaseData;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 仓库实体类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_warehouse")
public class Warehouse implements Serializable, BaseData {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 仓库id
     */
    @TableId(value = "warehouse_id", type = IdType.AUTO)
    private Integer warehouseId;

    /**
     * 仓库名称
     */
    @TableField("warehouse_name")
    private String warehouseName;

    /**
     * 仓库面积
     */
    @TableField("area")
    private Integer area;

    /**
     * 仓库描述
     */
    @TableField("description")
    private String description;

    /**
     * 仓库更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    @TableLogic
    private Short deleted;

    // 关联账户ID列表
    @TableField(exist = false)
    private List<Integer> accountIds;
}
