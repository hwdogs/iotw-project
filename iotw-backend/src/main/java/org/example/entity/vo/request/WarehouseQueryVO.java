package org.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

/**
 * 仓库请求表格vo类
 *
 * @author hwshou
 * @date 2025/5/31  12:29
 */
@Data
public class WarehouseQueryVO {
    // 分页参数
    @Min(1)
    private Integer pageNum = 1;

    @Range(min = 5, max = 50)
    private Integer pageSize = 10;

    // 查询条件
    private String warehouseName;   //仓库名称模糊查询
    private Integer startArea;       //仓库占地范围查询
    private Integer endArea;
    private String startUpdateTime; //更新时间范围查询
    private String endUpdateTime;

    //排序字段
    private String sortField = "area";
    private Boolean sortAsc = false; // 默认降序
}
