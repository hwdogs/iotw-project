package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.example.entity.BaseUserEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 顾客实体类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_customer")
public class Customer extends BaseUserEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 顾客id
     */
    @TableId(value = "customer_id", type = IdType.ASSIGN_ID)
    private Integer customerId;

    /**
     * 顾客名称
     */
    @TableField("username")
    private String username;

    /**
     * 顾客密码
     */
    @TableField("password")
    private String password;

    /**
     * 顾客邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 顾客电话号码
     */
    @TableField("phone")
    private String phone;

    /**
     * 顾客地址
     */
    @TableField("address")
    private String address;

    /**
     * 顾客生日
     */
    @TableField("birth")
    private LocalDate birth;

    /**
     * 顾客性别
     */
    @TableField("sex")
    private Short sex;

    /**
     * 顾客注册时间
     */
    @TableField("register_time")
    private LocalDateTime registerTime;

    /**
     * 顾客更新时间
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
