package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.entity.DTOConverter;

/**
 * 回应类别列表查询vo类
 *
 * @author hwshou
 * @date 2025/6/6  17:48
 */
@Data
@AllArgsConstructor
public class CategoryTableVO implements DTOConverter {
    private Short categoryId;
    private String categoryName;
}
