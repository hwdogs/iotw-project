package org.example.entity.vo.request;

import lombok.Data;
import org.example.entity.DTOConverter;

/**
 * 请求新增管理记录vo类
 *
 * @author hwshou
 * @date 2025/6/3  23:47
 */
@Data
public class ManageAddVO implements DTOConverter {
    private Integer warehouseId;
    private Integer accountId;
}
