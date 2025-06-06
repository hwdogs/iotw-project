package org.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 请求查询类别 vo
 *
 * @author hwshou
 * @date 2025/5/28  22:34
 */
@Data
public class CategoryQueryVO {
    // 分页参数
    @Min(1)
    private Integer pageNum = 1;

    @Range(min = 5, max = 50)
    private Integer pageSize = 10;


    private Short categoryId;     //id右模糊查询
    private String categoryName;    // 类别名称模糊查询

    private Boolean sortAsc = false; // 默认降序
}
