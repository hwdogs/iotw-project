package org.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * supplier供应商前端请求列表请求类
 *
 * @author hwshou
 * @date 2025/6/4  18:03
 */
@Data
public class SupplierQueryVO {
    // 分页参数
    @Min(1)
    private Integer pageNum = 1;

    @Range(min = 5, max = 50)
    private Integer pageSize = 10;

    private Integer supplierId;     // id模糊查询
    private String username;    // name模糊查询
    private String email;           // email精确查询
    private String phone;           // phone精确查询
    private String address;         // address模糊擦洗
    private Short sex;              // sex精确查询

    private LocalDate startBirth;        //生日范围查询
    private LocalDate endBirth;
    private LocalDateTime startRegisterTime;  // 创建时间范围查询
    private LocalDateTime endRegisterTime;
    private LocalDateTime startUpdateTime;  //更新时间范围查询
    private LocalDateTime endUpdateTime;

    // 排序字段
    private String sortField = "update_time";
    private Boolean sortAsc = false; // 默认降序
}
