package org.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 返回的请求顾客列表结果VO类
 *
 * @author hwshou
 * @date 2025/6/6  00:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTableVO {
    private Integer customerId;
    private String username;
    private String email;
    private String phone;
    private String address;
    private Short sex;
    private LocalDate birth;
    private LocalDateTime registerTime;
    private LocalDateTime updateTime;
}
