package org.example.entity.vo.request;

import lombok.Data;
import org.example.entity.DTOConverter;

import java.util.List;

/**
 * 请求新增仓库VO类
 *
 * @author hwshou
 * @date 2025/6/1  23:20
 */
@Data
public class WarehouseAddVO implements DTOConverter {
    private String warehouseName;
    private List<Integer> accountIds;  // 从manage表更新
    private Integer area;
    private String description;
}
