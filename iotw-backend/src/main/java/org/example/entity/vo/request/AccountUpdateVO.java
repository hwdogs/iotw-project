package org.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.DTOConverter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

/**
 * @author hwshou
 * @date 2025/5/29  13:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateVO implements DTOConverter {
    Integer id;
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 10)
    String username;
    Integer role;
    LocalDate birth;
    @Range(min = 0, max = 1, message = "性别值只能是0或1")
    Integer sex;
    @Email
    @Length(min = 4)
    String email;
    String address;
}
