package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 返回前端的manage表格vo类
 *
 * @author hwshou
 * @date 2025/6/3  23:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManageTableVO {
    private Integer manageId;
    private Integer warehouseId;
    private Integer accountId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
