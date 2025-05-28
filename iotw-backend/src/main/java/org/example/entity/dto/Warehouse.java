package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
/**
 * <p>
 * 
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Getter
@Setter
@ToString
@TableName("t_warehouse")
public class Warehouse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 仓库id
     */
    @TableId("warehouse_id")
    private Integer warehouseId;

    /**
     * 仓库名称
     */
    @TableField("warehouse_name")
    private String warehouseName;

    /**
     * 仓库负责人
     */
    @TableField("account_id")
    private Integer accountId;

    /**
     * 仓库面积
     */
    @TableField("area")
    private Integer area;

    /**
     * 仓库描述
     */
    @TableField("describe")
    private String describe;

    /**
     * 仓库更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Short deleted;
}
