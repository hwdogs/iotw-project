package org.example.entity.vo.request;

import lombok.Data;
import org.example.entity.DTOConverter;

/**
 * 添加供货记录vo类
 *
 * @author hwshou
 * @date 2025/6/6  21:12
 */
@Data
public class SupplyAddVO implements DTOConverter {
    private Integer supplierId;
    private Integer goodId;
    private Integer supplyNumber;
}
