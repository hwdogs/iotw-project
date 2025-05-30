package org.example.entity.vo.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author hwshou
 * @date 2025/5/19  16:09
 */
@Data
public class AuthorizeVO {
    String username;
    Short role;
    String token;
    Date expires;
    Short sex;
    LocalDate birthday;
    LocalDateTime lastLogin;
}
