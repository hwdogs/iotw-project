package org.example.entity.vo.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

/**
 * 新增supplier的请求类
 *
 * @author hwshou
 * @date 2025/6/5  00:53
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SupplierAddVO extends AccountEmailRegisterVO{
    private String phone;
    private String address;
    private LocalDate birth;
    private Short sex;
}
