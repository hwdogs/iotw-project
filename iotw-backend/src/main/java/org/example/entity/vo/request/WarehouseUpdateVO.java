package org.example.entity.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 更新仓库请求vo类
 *
 * @author hwshou
 * @date 2025/5/31  17:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseUpdateVO extends WarehouseAddVO {
    private Integer warehouseId;

}
