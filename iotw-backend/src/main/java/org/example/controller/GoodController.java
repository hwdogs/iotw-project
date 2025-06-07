package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.vo.request.GoodAddVO;
import org.example.entity.vo.request.GoodQueryVO;
import org.example.entity.vo.request.GoodUpdateVO;
import org.example.entity.vo.response.GoodTableVO;
import org.example.service.GoodService;
import org.example.utils.ResponseUtils;
import org.springframework.web.bind.annotation.*;

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

    @Resource
    private ResponseUtils responseUtils;

    /**
     * 请求货物信息
     *
     * @param vo 请求信息
     * @return 响应对应vo类
     */
    @PostMapping("/query")
    public RestBean<IPage<GoodTableVO>> queryTableByConditions(@Valid @RequestBody GoodQueryVO vo) {
        return RestBean.success(goodService.queryGoodTableByConditions(vo));
    }

    /**
     * 逻辑删除商品
     *
     * @param id 需要逻辑删除的商品id
     * @return 是否删除成功
     */
    @GetMapping("/delete")
    public RestBean<Void> deleteOneGood(@RequestParam Integer id) {
        return responseUtils.messageHandle(id, goodService::logicDeleteOneGood);
    }

    /**
     * 添加一个商品
     *
     * @param vo 商品信息
     * @return 是否添加成功
     */
    @PostMapping("/add")
    public RestBean<Void> addOneGood(@RequestBody GoodAddVO vo) {
        return responseUtils.messageHandle(vo, goodService::AddOneGood);
    }

    /**
     * 更新一个商品
     *
     * @param vo 更新商品信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public RestBean<Void> updateOneGood(@RequestBody GoodUpdateVO vo) {
        return responseUtils.messageHandle(vo, goodService::updateOneGood);
    }
}
