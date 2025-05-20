package org.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @author hwshou
 * @date 2025/5/19  16:09
 */
@Data
public class AuthorizeVO {
    String username;
    Integer role;
    String token;
    Date expires;
}
