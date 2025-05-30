package org.example.entity.vo.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.entity.Verifiable;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;

/**
 * @author hwshou
 * @date 2025/5/30 12:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountAddVO extends EmailRegisterVO implements Verifiable {
    @TableField("role")
    @JsonProperty("role")
    private Short role;

    @TableField("birth")
    @JsonProperty("birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;

    @TableField("address")
    @JsonProperty("address")
    private String address;

    @TableField("sex")
    @JsonProperty("sex")
    private Short sex;
}
