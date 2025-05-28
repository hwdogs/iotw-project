package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_customer")
public class Customer implements Serializable {

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
    @TableField("customer_name")
    private String customerName;

    /**
     * 顾客密码
     */
    @TableField("customer_password")
    private String customerPassword;

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
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 顾客更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Short deleted;
}
