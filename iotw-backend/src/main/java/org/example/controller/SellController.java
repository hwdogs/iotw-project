package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.vo.request.SellAddVO;
import org.example.entity.vo.request.SellQueryVO;
import org.example.entity.vo.request.SellUpdateVO;
import org.example.entity.vo.response.SellTableVO;
import org.example.service.SellService;
import org.example.utils.ResponseUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * sell出库前端控制器
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@RestController
@RequestMapping("/api/sell")
public class SellController {
    @Resource
    private SellService sellService;

    @Resource
    private ResponseUtils responseUtils;

    /**
     * 请求出库列表信息
     *
     * @param vo 请求信息
     * @return 响应对应vo类
     */
    @PostMapping("/query")
    public RestBean<IPage<SellTableVO>> querySellTableByCondition(
            @Valid @RequestBody SellQueryVO vo) {
        return responseUtils.dataHandle(
                vo,
                sellService::querySellTableByCondition,
                result -> result == null ? "内部错误" : null
        );
    }


}
