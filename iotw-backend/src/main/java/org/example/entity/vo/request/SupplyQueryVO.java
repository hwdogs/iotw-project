package org.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * supply供货前端请求列表请求类
 *
 * @author hwshou
 * @date 2025/6/6  20:35
 */
@Data
public class SupplyQueryVO {
    // 分页参数
    @Min(1)
    private Integer pageNum = 1;

    @Range(min = 5, max = 50)
    private Integer pageSize = 10;

    private Integer supplyId;       // 供货id模糊右查询
    private Integer supplierId;     // 供货商id
    private Integer goodId;         // 货物id
    private Short status;           // 货物状态

    private Integer startSupplyNumber;   // 货物数量范围查询
    private Integer endSupplyNumber;
    private String startCreateTime;  // 进货时间范围查询
    private String endCreateTime;
    private String startUpdateTime;  // 更新时间范围查询
    private String endUpdateTime;

    // 排序字段
    private String sortField = "create_time";
    private Boolean sortAsc = false; // 默认降序
}
