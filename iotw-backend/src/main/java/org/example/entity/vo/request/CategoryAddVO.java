package org.example.entity.vo.request;

import lombok.Data;
import org.example.entity.DTOConverter;

/**
 * 添加类别请求vo类
 *
 * @author hwshou
 * @date 2025/6/6  18:05
 */
@Data
public class CategoryAddVO implements DTOConverter {
    private Short categoryId;
    private String categoryName;
}
