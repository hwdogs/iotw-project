package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 管理实体类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-31 14:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_manage")
public class Manage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 管理表id
     */
    @TableId("manage_id")
    private Integer manageId;

    /**
     * 管理的仓库表id
     */
    @TableField("warehouse_id")
    private Integer warehouseId;

    /**
     * 管理仓库账户id
     */
    @TableField("account_id")
    private Integer accountId;

    /**
     * 创建时间
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
