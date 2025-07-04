package org.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 请求查询顾客 vo
 *
 * @author hwshou
 * @date 2025/5/28  22:35
 */
@Data
public class CustomerQueryVO {
    // 分页参数
    @Min(1)
    private Integer pageNum = 1;

    @Range(min = 5, max = 50)
    private Integer pageSize = 10;

    private Integer customerId; //id模糊查询
    private String username;    //username模糊查询
    private String email;       //email精确查询
    private String phone;       //phone精确查询
    private String address;     //address模糊查询
    private Short sex;         //sex精确查询

    private String startBirth;    //生日范围查询
    private String endBirth;
    private String startRegisterTime;  // 创建时间范围查询
    private String endRegisterTime;
    private String startUpdateTime;  //更新时间范围查询
    private String endUpdateTime;

    // 排序字段
    private String sortField = "update_time";
    private Boolean sortAsc = false; // 默认降序
}
