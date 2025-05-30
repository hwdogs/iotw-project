package org.example.entity;

import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * 可以快速转化为指定的vo对象
 *
 * @author hwshou
 * @date 2025/5/29  14:36
 */
public interface DTOConverter {
    default <D> D asDTO(Class<D> dtoClass, Consumer<D> postProcessor) {
        try {
            D dto = dtoClass.getDeclaredConstructor().newInstance();
            // 关键点：遍历VO字段复制到DTO
            for (Field voField : this.getClass().getDeclaredFields()) {
                copyField(voField, dto, dtoClass);
            }
            postProcessor.accept(dto);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <D> void copyField(Field voField, D dto, Class<D> dtoClass) {
        try {
            Field dtoField = dtoClass.getDeclaredField(voField.getName());
            voField.setAccessible(true);
            dtoField.setAccessible(true);
            Object value = voField.get(this);
            dtoField.set(dto, value);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // 忽略不存在的字段
        }
    }
}
