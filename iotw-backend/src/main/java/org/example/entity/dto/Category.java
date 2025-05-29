package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * <p>
 * 类别实体类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_category")
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 类别id
     */
    @TableId(value = "category_id", type = IdType.AUTO)
    private Short categoryId;

    /**
     * 类别名称
     */
    @TableField("category_name")
    private String categoryName;

    /**
     * 是否删除
     */
    @TableField("deleted")
    @TableLogic
    private Short deleted;
}
