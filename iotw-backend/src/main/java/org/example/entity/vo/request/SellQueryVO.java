package org.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 请求出库列表vo类
 *
 * @author hwshou
 * @date 2025/6/6  23:50
 */
@Data
public class SellQueryVO {
    // 分页参数
    @Min(1)
    private Integer pageNum = 1;

    @Range(min = 5, max = 50)
    private Integer pageSize = 10;

    private Integer sellId;         // 入库id模糊右查询
    private Integer customerId;     // 顾客id模糊右查询
    private Integer goodId;         // 货物id模糊右查询

    private Integer startSellNumber;    //入库数量范围查询
    private Integer endSellNumber;
    private String startCreateTime;  //入库时间范围查询
    private String endCreateTime;
    private String startUpdateTime;  //更新时间范围查询
    private String endUpdateTime;

    // 排序字段
    private String sortField = "create_time";
    private Boolean sortAsc = false; // 默认降序
}
