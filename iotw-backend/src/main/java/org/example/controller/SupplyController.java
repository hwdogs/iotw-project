package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.vo.request.SupplyAddVO;
import org.example.entity.vo.request.SupplyQueryVO;
import org.example.entity.vo.request.SupplyUpdateVO;
import org.example.entity.vo.response.SupplyTableVO;
import org.example.service.SupplyService;
import org.example.utils.ResponseUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * supply入库前端控制器
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@RestController
@RequestMapping("/api/supply")
public class SupplyController {

    @Resource
    private SupplyService supplyService;

    @Resource
    private ResponseUtils responseUtils;

    /**
     * 请求入库列表信息
     *
     * @param vo 请求信息
     * @return 响应对应vo类
     */
    @PostMapping("/query")
    public RestBean<IPage<SupplyTableVO>> querySupplyTableByCondition(
            @Valid @RequestBody SupplyQueryVO vo) {
        return responseUtils.dataHandle(
                vo,
                supplyService::querySupplyTableByCondition,
                result -> result == null ? "内部错误" : null
        );
    }

    /**
     * 添加一条入库信息
     *
     * @param vo 入库信息
     * @return 是否添加成功
     */
    @PostMapping("/add")
    public RestBean<Void> addOneSupply(@Valid @RequestBody SupplyAddVO vo) {
        return responseUtils.messageHandle(vo, supplyService::addOneSupply);
    }

    /**
     * 更新一条入库信息
     *
     * @param vo 更新信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public RestBean<Void> updateOneSupply(@Valid @RequestBody SupplyUpdateVO vo) {
        return responseUtils.messageHandle(vo, supplyService::updateOneSupply);
    }

}
