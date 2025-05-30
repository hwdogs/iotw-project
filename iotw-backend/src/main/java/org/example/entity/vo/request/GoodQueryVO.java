package org.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 请求查询货物 vo
 *
 * @author hwshou
 * @date 2025/5/28  22:39
 */
@Data
public class GoodQueryVO {
    // 分页参数
    @Min(1)
    private Integer pageNum = 1;

    @Range(min = 5, max = 50)
    private Integer pageSize = 10;

    //查询条件
    private String goodName;        //货物名称模糊查询
    private Integer warehouseId;    //仓库筛选
    private Short categoryId;       //类别筛选
    private BigDecimal startPrice;     //价格范围查询
    private BigDecimal endPrice;
    private LocalDateTime startCreateTime;  //添加时间范围查询
    private LocalDateTime endCreateTime;

    //排序字段
    private String sortField = "create_time";
    private Boolean sortAsc = false; // 默认降序

}
