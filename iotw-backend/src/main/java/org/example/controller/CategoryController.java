package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.vo.request.CategoryAddVO;
import org.example.entity.vo.request.CategoryQueryVO;
import org.example.entity.vo.request.CategoryUpdateVO;
import org.example.entity.vo.response.CategoryTableVO;
import org.example.service.CategoryService;
import org.example.utils.ResponseUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * category前端控制器
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @Resource
    private ResponseUtils responseUtils;

    /**
     * 请求类别信息列表
     *
     * @param vo 请求信息
     * @return 响应对象vo类
     */
    @PostMapping("/query")
    public RestBean<IPage<CategoryTableVO>> queryCategoryTableByCondition(@RequestBody CategoryQueryVO vo) {
        return RestBean.success(categoryService.queryCategoryTableByCondition(vo));
    }

    /**
     * 添加一条类别记录
     *
     * @param vo 类别信息
     * @return 是否添加成功
     */
    @PostMapping("/add")
    public RestBean<Void> addOneCategory(@RequestBody CategoryAddVO vo) {
        return responseUtils.messageHandle(vo, categoryService::addOneCategory);
    }

}
