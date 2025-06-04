package org.example.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体基类
 *
 * @author hwshou
 * @date 2025/6/4  22:28
 */
@Data
public class BaseUserEntity {
    private LocalDateTime registerTime;
    private LocalDateTime updateTime;
}
