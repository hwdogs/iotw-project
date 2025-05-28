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
@TableName("t_sold")
public class Sold implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 售出订单id
     */
    @TableId("sold_id")
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
