package org.example.entity.vo.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新出库信息vo类
 *
 * @author hwshou
 * @date 2025/6/7  00:24
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SellUpdateVO extends SellAddVO {
    private Integer sellId;
}
