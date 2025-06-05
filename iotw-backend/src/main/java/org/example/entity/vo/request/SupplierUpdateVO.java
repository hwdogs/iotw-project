package org.example.entity.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.example.entity.DTOConverter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

/**
 * 供应商请求更新类
 *
 * @author hwshou
 * @date 2025/6/5  12:33
 */
@Data
public class SupplierUpdateVO implements DTOConverter {
    private Integer supplierId;

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 1, max = 10)
    private String username;

    @Email
    @Length(min = 4)
    private String email;

    private String phone;

    private String address;

    @Range(min = 0, max = 1, message = "性别值只能是0或1")
    private Short sex;

    private LocalDate birth;

}
