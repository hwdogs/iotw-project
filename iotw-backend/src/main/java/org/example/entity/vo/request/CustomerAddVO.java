package org.example.entity.vo.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 新增customer的请求类
 *
 * @author hwshou
 * @date 2025/6/6  14:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerAddVO extends AccountEmailRegisterVO {
    private String phone;
    private String address;
    private LocalDate birth;
    private Short sex;
}
