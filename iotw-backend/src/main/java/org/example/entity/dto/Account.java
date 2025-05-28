package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @date 2025/5/19  21:31
 */
@Data
@TableName("db_account")
@AllArgsConstructor
@NoArgsConstructor
public class Account implements BaseData {
    @TableId(type = IdType.ASSIGN_ID)
    Integer id;
    String username;
    String password;
    String email;
    Integer role;
    LocalDateTime registerTime;
    LocalDate birth;
    String address;
    Integer sex;
    LocalDateTime lastLoginTime;
    LocalDateTime updateTime;
    Integer deleted;
}
