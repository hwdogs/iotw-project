package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 返回仓库表格查询vo类
 *
 * @author hwshou
 * @date 2025/5/31  12:37
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseTableVO {
    private Integer warehouseId;
    private String warehouseName;
    private Integer accountId;
    private Integer area;
    private String description;
    private LocalDateTime updateTime;
}
