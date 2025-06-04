package org.example.entity;

/**
 * 用户实体操作上下文接口
 *
 * @author hwshou
 * @date 2025/6/4  22:30
 */
public interface UserEntityContext<E extends BaseUserEntity> {
    /**
     * 保存用户实体
     */
    boolean saveUser(E entity);

    /**
     * 检查邮箱是否已存在
     */
    boolean existsUserEmail(String email);

    /**
     * 检查用户名是否已存在
     */
    boolean existsUsername(String username);
}
