package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
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
@TableName("t_supplier")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 供应商id
     */
    @TableId("supplier_id")
    private Integer supplierId;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplierName;

    /**
     * 供应商密码
     */
    @TableField("supplier_password")
    private String supplierPassword;

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
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 供应商更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @TableField("deleted")
    private Short deleted;
}
