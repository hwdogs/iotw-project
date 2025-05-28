package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 售出表属性实体类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_sold")
public class Sold implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 售出订单id
     */
    @TableId(value = "sold_id", type = IdType.ASSIGN_ID)
    private Integer soldId;

    /**
     * 顾客id
     */
    @TableField("customer_id")
    private Integer customerId;

    /**
     * 商品id
     */
    @TableField("good_id")
    private Integer goodId;

    /**
     * 售出数量
     */
    @TableField("sold_number")
    private Integer soldNumber;

    /**
     * 售出时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Short deleted;
}
