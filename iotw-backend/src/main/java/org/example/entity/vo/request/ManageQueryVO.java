package org.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

/**
 * manage管理前端请求列表请求类
 *
 * @author hwshou
 * @date 2025/5/31  17:48
 */
@Data
public class ManageQueryVO {
    // 分页参数
    @Min(1)
    private Integer pageNum = 1;

    @Range(min = 5, max = 50)
    private Integer pageSize = 10;

    private Integer warehouseId;
    private Integer accountId;
    private LocalDateTime startCreateTime;
    private LocalDateTime endCreateTime;
    private LocalDateTime startUpdateTime;
    private LocalDateTime endUpdateTime;

    // 排序字段
    private String sortField = "create_time";
    private Boolean sortAsc = false; // 默认降序

}
