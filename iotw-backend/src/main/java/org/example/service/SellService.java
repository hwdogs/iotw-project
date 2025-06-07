package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Sell;
import org.example.entity.vo.request.SellAddVO;
import org.example.entity.vo.request.SellQueryVO;
import org.example.entity.vo.request.SellUpdateVO;
import org.example.entity.vo.response.SellTableVO;

/**
 * <p>
 * sell出库服务类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
public interface SellService extends IService<Sell> {
    IPage<SellTableVO> querySellTableByCondition(SellQueryVO vo);


}
