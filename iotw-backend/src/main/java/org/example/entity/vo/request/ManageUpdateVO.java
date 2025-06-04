package org.example.entity.vo.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hwshou
 * @date 2025/6/4 00:14
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ManageUpdateVO extends ManageAddVO {
    private Integer manageId;
}
