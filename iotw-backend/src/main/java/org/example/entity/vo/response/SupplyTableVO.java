package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 回应前端请求进货信息列表的vo类
 *
 * @author hwshou
 * @date 2025/6/6  20:42
 */
@Data
@AllArgsConstructor
public class SupplyTableVO {
    private Integer supplyId;
    private Integer supplierId;
    private Integer goodId;
    private Integer supplyNumber;
    private Short status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
