package org.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.entity.UserVerifiable;
import org.hibernate.validator.constraints.Length;

/**
 * @author hwshou
 * @date 2025/5/25  00:05
 */
@Data
public class AccountEmailRegisterVO implements UserVerifiable {
    @Email
    @Length(min = 4)
    String email;
    @Length(max = 6, min = 6)
    String code;
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5_]+$")
    @Length(min = 1, max = 10)
    String username;
    @Length(min = 6, max = 20)
    String password;
}
