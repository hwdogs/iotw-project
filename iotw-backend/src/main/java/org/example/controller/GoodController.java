package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.vo.request.GoodQueryVO;
import org.example.entity.vo.response.GoodTableVO;
import org.example.service.GoodService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * good前端控制器
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@RestController
@RequestMapping("/api/good")
public class GoodController {

    @Resource
    private GoodService goodService;

    @PostMapping("/query")
    public RestBean<IPage<GoodTableVO>> query(@Valid @RequestBody GoodQueryVO vo) {
        return RestBean.success(goodService.queryGoodTableByConditions(vo));
    }
}
