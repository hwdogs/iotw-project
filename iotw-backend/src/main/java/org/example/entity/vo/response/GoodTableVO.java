package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 查询货物列表返回的vo类
 *
 * @author hwshou
 * @since  2025/5/28  22:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodTableVO {
    private Integer GoodId;
    private String goodName;
    private Integer warehouseId;
    private Short category;
    private BigDecimal price;
    private String standard;
    private String description;
    private String image;
    private LocalDateTime createTime;
}
