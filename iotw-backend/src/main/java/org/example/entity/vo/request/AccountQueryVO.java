package org.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;

/**
 * account条件查询dto类
 *
 * @author hwshou
 * @date 2025/5/27  13:24
 */
@Data
public class AccountQueryVO {
    // 分页参数
    @Min(1)
    private Integer pageNum = 1;

    @Range(min = 5, max = 50)
    private Integer pageSize = 10;

    // 查询条件
    private Integer id;       // 模糊查询账户id
    private String username;  // 模糊查询用户名
    private Integer role;      // 精确匹配角色
    private Integer sex;       // 性别筛选
    private String startBirth; // 出生日期范围查询
    private String endBirth;
    private String email;   // 模糊查询邮箱

    // 排序字段
    private String sortField = "update_time";
    private Boolean sortAsc = false; // 默认降序
}
