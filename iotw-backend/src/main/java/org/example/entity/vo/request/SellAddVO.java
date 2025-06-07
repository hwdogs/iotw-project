package org.example.entity.vo.request;

import lombok.Data;
import org.example.entity.DTOConverter;

/**
 * 新增出库请求vo类
 *
 * @author hwshou
 * @date 2025/6/7  00:13
 */
@Data
public class SellAddVO implements DTOConverter {
    private Integer customerId;
    private Integer goodId;
    private Integer sellNumber;
}
