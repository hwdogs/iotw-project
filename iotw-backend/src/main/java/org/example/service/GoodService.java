package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.entity.dto.Good;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.vo.request.GoodQueryVO;
import org.example.entity.vo.response.GoodTableVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
public interface GoodService extends IService<Good> {
    IPage<GoodTableVO> queryGoodTableByConditions(GoodQueryVO vo);

    String AddOneGood(Good good);
}
