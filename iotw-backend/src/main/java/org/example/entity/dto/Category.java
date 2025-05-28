package org.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
/**
 * <p>
 * 
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Getter
@Setter
@ToString
@TableName("t_category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 类别id
     */
    @TableId("category_id")
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
    private Short deleted;
}
