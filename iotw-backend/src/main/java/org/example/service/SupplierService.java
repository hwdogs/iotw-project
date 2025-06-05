package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.entity.dto.Supplier;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.vo.request.*;
import org.example.entity.vo.response.SupplierTableVO;

/**
 * <p>
 * supplier服务类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
public interface SupplierService extends IService<Supplier> {
    IPage<SupplierTableVO> querySupplierTableByCondition(SupplierQueryVO vo);

    String logicDeleteOneSupplier(Integer id);

    String registerOneSupplier(AccountEmailRegisterVO vo);

    String addOneSupplier(SupplierAddVO vo);

    String updateOneSupplier(SupplierUpdateVO vo);
}
