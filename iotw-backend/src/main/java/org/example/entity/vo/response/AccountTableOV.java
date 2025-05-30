package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * 查询账户列表返回的vo类
 *
 * @author hwshou
 * @date 2025/5/27 12:30
 */
@Data
@AllArgsConstructor
public class AccountTableOV {
    Integer id;
    String name;
    Short role;
    LocalDate birth;
    Short sex;
    String email;
    String address;
}
