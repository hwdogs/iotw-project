package org.example.entity;

import java.util.function.Function;

/**
 * 用户更新操作上下文接口
 *
 * @author hwshou
 * @date 2025/6/5  12:48
 */
public interface UserUpdateContext<E extends BaseUserEntity> {
    // 从VO中获取ID的函数
    <V> Function<V, Integer> getIdGetter();

    // 根据ID获取实体
    E getUserById(Integer id);

    // 检查邮箱是否被其他用户占用（排除指定ID）
    boolean existsUserEmailExcludingId(String email, Integer excludeId);

    // 执行更新操作
    boolean updateUser(E entity);
}
