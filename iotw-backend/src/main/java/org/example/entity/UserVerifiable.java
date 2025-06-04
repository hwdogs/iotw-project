package org.example.entity;

/**
 * @author hwshou
 * @date 2025/5/30  12:39
 */
public interface UserVerifiable extends DTOConverter{
    String getEmail();
    String getUsername();
    String getCode();
}
