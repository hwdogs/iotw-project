package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author hwshou
 * @date 2025/5/27  12:30
 */
@Data
@AllArgsConstructor
public class AccountTableOV {
    Integer id;
    String name;
    Integer role;
    LocalDate birth;
    Integer sex;
    String email;
    String address;
}
