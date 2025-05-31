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

    @Resource
    private ResponseUtils responseUtils;

    /**
     * 请求仓库表格信息
     *
     * @param vo 请求信息
     * @return 响应对应vo类
     */
    @PostMapping("/query")
    public RestBean<IPage<WarehouseTableVO>> queryTableByConditions(@RequestBody WarehouseQueryVO vo) {
        return RestBean.success(warehouseService.queryWarehouseTableByConditions(vo));
    }
}
