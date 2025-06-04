package org.example.entity.exception;

/**
 * @author hwshou
 * @date 2025/6/4  22:40
 */
public class UserOperationException extends RuntimeException {
    public UserOperationException(String message) {
        super(message);
    }

    public UserOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
