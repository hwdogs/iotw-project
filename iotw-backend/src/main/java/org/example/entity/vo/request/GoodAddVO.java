package org.example.entity.vo.request;

import lombok.Data;
import org.example.entity.DTOConverter;

import java.math.BigDecimal;

/**
 * 添加商品VO类
 *
 * @author hwshou
 * @date 2025/6/7  21:20
 */
@Data
public class GoodAddVO implements DTOConverter {
    private Integer goodId;
    private String goodName;
    private Integer warehouseId;
    private Short category;
    private BigDecimal price;
    private String standard;
    private String description;
    private String image;
}
