package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import org.example.entity.BaseUserEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 供应商实体类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_supplier")
public class Supplier extends BaseUserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 供应商id
     */
    @TableId(value = "supplier_id", type = IdType.ASSIGN_ID)
    private Integer supplierId;

    /**
     * 供应商名称
     */
    @TableField("username")
    private String username;

    /**
     * 供应商密码
     */
    @TableField("password")
    private String password;

    /**
     * 供应商邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 供应商电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 供应商地址
     */
    @TableField("address")
    private String address;

    /**
     * 供应商生日
     */
    @TableField("birth")
    private LocalDate birth;

    /**
     * 供应商性别
     */
    @TableField("sex")
    private Short sex;

    /**
     * 供应商注册时间
     */
    @TableField("register_time")
    private LocalDateTime registerTime;

    /**
     * 供应商更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    @TableLogic
    private Short deleted;
}
