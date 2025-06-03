package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.vo.request.WarehouseQueryVO;
import org.example.entity.vo.response.WarehouseTableVO;
import org.example.service.WarehouseService;
import org.example.utils.ResponseUtils;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 逻辑删除仓库
     *
     * @param id 需要删除的仓库id
     * @return 是否删除成功
     */
    @GetMapping("/delete")
    public RestBean<Void> deleteOneWarehouse(@RequestParam Integer id) {
        return responseUtils.messageHandle(id, warehouseService::logicDeleteOneWarehouseRecord);
    }

    /**
     * 更新仓库信息，可以顺带更新manage表
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public RestBean<Void> updateOneWarehouse(@RequestBody WarehouseUpdateVO vo) {
        return responseUtils.messageHandle(vo, warehouseService::updateOneWarehouse);
    }

    /**
     * 新增仓库
     *
     * @param vo 新增仓库信息
     * @return 是否新增成功
     */
    @PostMapping("/add")
    public RestBean<Void> addOneWarehouse(@RequestBody WarehouseAddVO vo) {
        return responseUtils.messageHandle(vo, warehouseService::addOneWarehouse);
    }
}
