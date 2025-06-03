package org.example.entity.vo.response;

import lombok.Data;

/**
 * 回复所有id和username的vo类
 *
 * @author hwshou
 * @date 2025/6/3  00:24
 */
@Data
public class AccountIdUsernameVO {
    private Integer id;
    private String username;
}
