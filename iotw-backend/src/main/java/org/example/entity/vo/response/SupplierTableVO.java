package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 返回前端的supplier表格vo类
 *
 * @author hwshou
 * @date 2025/6/4  18:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupplierTableVO {
    private Integer supplierId;
    private String username;
    private String email;
    private String phone;
    private String address;
    private Short sex;
    private LocalDate birth;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
