package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * <p>
 * 供应表属性实体类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_supply")
public class Supply implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 进货记录
     */
    @TableId(value = "supply_id", type = IdType.ASSIGN_ID)
    private Integer supplyId;

    /**
     * 进货商id
     */
    @TableField("supplier_id")
    private Integer supplierId;

    /**
     * 进货物品id
     */
    @TableField("good_id")
    private Integer goodId;

    /**
     * 进货数量
     */
    @TableField("supply_number")
    private Integer supplyNumber;

    /**
     * 进货时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 进货状态 0:未审批 1:已审批 -1:未通过
     */
    @TableField("status")
    private Short status;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Short deleted;
}
