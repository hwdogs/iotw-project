package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import org.example.entity.RestBean;
import org.example.entity.vo.request.ManageAddVO;
import org.example.entity.vo.request.ManageQueryVO;
import org.example.entity.vo.response.ManageTableVO;
import org.example.entity.vo.request.ManageUpdateVO;
import org.example.service.ManageService;
import org.example.utils.ResponseUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * manage前端控制器
 * </p>
 *
 * @author hwshou
 * @since 2025-05-31 14:51
 */
@RestController
@RequestMapping("/api/manage")
public class ManageController {

    @Resource
    private ManageService manageService;

    @Resource
    private ResponseUtils responseUtils;

    /**
     * 请求管理信息
     *
     * @param vo 请求信息
     * @return 响应对应vo类
     */
    @PostMapping("/query")
    public RestBean<IPage<ManageTableVO>> queryTableByConditions(@RequestBody ManageQueryVO vo) {
        return RestBean.success(manageService.queryManageTableByCondition(vo));
    }

    /**
     * 添加一条管理记录
     *
     * @param vo 管理信息
     * @return 是否管理成功
     */
    @PostMapping("/add")
    public RestBean<Void> addOneManage(@RequestBody ManageAddVO vo) {
        return responseUtils.messageHandle(vo, manageService::addOneManage);
    }

    /**
     * 更新一条管理记录
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public RestBean<Void> updateOneManage(@RequestBody ManageUpdateVO vo) {
        return responseUtils.messageHandle(vo, manageService::updateOneManage);
    }

    /**
     * 删除一条管理记录
     *
     * @param id 需要删除记录的id
     * @return 是否删除成功
     */
    @GetMapping("/delete")
    public RestBean<Void> deleteOneManage(@RequestParam Integer id) {
        return responseUtils.messageHandle(id, manageService::logicDeleteOneManage);
    }
}
