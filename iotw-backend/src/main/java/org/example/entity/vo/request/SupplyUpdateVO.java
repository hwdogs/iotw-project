package org.example.entity.vo.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新进货信息vo类
 *
 * @author hwshou
 * @date 2025/6/6  21:29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SupplyUpdateVO extends SupplyAddVO {
    private Integer supplyId;
    private Short status;
}
