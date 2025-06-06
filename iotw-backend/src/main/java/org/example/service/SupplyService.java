package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Supply;
import org.example.entity.vo.request.SupplyAddVO;
import org.example.entity.vo.request.SupplyQueryVO;
import org.example.entity.vo.request.SupplyUpdateVO;
import org.example.entity.vo.response.SupplyTableVO;

/**
 * <p>
 * supply入库服务类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
public interface SupplyService extends IService<Supply> {
    IPage<SupplyTableVO> querySupplyTableByCondition(SupplyQueryVO vo);

    String addOneSupply(SupplyAddVO vo);

    String updateOneSupply(SupplyUpdateVO vo);

    String logicDeleteOneSupply(Integer supplyId);
}
