package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author hwshou
 * @date 2025/5/19  21:31
 */
@Data
@TableName("db_account")
@AllArgsConstructor
public class Account {
    @TableId(type = IdType.ASSIGN_ID)
    Integer id;
    String username;
    String password;
    String email;
    Integer role;
    LocalDateTime registerTime;
}
