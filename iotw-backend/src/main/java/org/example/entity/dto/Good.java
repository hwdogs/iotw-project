package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 货物实体类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_good")
public class Good implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 货物id
     */
    @TableId(value = "good_id", type = IdType.ASSIGN_ID)
    private Integer goodId;

    /**
     * 货物名称
     */
    @TableField("good_name")
    private String goodName;

    /**
     * 存入的仓库id
     */
    @TableField("warehouse_id")
    private Integer warehouseId;

    /**
     * 货物类别
     */
    @TableField("category")
    private Short category;

    /**
     * 货物价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 货物状态
     */
    @TableField("standard")
    private String standard;

    /**
     * 货物描述
     */
    @TableField("description")
    private String description;

    /**
     * 货物照片
     */
    @TableField("image")
    private String image;

    /**
     * 货物产生时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    @TableLogic
    private Short deleted;
}
