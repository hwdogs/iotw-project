package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.BaseData;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 账户实体类
 *
 * @author hwshou
 * @date 2025/5/19 21:31
 */
@Data
@TableName("t_account")
@AllArgsConstructor
@NoArgsConstructor
public class Account implements BaseData {
    @TableId(type = IdType.ASSIGN_ID)
    private Integer id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("email")
    private String email;

    @TableField("role")
    private Short role;

    @TableField("register_time")
    private LocalDateTime registerTime;

    @TableField("birth")
    private LocalDate birth;

    @TableField("address")
    private String address;

    @TableField("sex")
    private Short sex;

    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    @TableField("update_time")
    private LocalDateTime updateTime;

    @TableLogic
    @TableField("deleted")
    private Short deleted;
}
