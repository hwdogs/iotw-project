package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.vo.request.WarehouseQueryVO;
import org.example.entity.vo.response.WarehouseTableVO;
import org.example.service.WarehouseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 仓库前端控制器
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@RestController
@RequestMapping("/api/warehouse")
public class WarehouseController {

    @Resource
    private WarehouseService warehouseService;

    @PostMapping("/query")
    public RestBean<IPage<WarehouseTableVO>> queryTableByConditions(@RequestBody WarehouseQueryVO vo) {
        return RestBean.success(warehouseService.queryWarehouseTableByConditions(vo));
    }
}
