package org.example.service.impl;

import org.example.entity.dto.Supplier;
import org.example.entity.vo.request.SupplierAddVO;
import org.example.mapper.SupplierMapper;
import org.example.service.SupplierService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    /**
     * 添加一名供应商
     *
     * @param vo 供应商信息
     * @return 是否添加成功
     */
    @Override
    public String addOneSupplier(SupplierAddVO vo) {
        return registerOrAddOneSupplier(vo);
    }
}
