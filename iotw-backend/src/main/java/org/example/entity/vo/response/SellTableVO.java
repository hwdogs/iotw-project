package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 回复请求入库列表vo类
 *
 * @author hwshou
 * @date 2025/6/6  23:55
 */
@Data
@AllArgsConstructor
public class SellTableVO {
    private Integer sellId;
    private Integer customerId;
    private Integer goodId;
    private Integer sellNumber;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
