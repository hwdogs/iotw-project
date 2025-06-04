package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.entity.dto.Manage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.vo.request.ManageAddVO;
import org.example.entity.vo.request.ManageQueryVO;
import org.example.entity.vo.response.ManageTableVO;
import org.example.entity.vo.request.ManageUpdateVO;

/**
 * <p>
 * manage服务类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-31 14:51
 */
public interface ManageService extends IService<Manage> {
    IPage<ManageTableVO> queryManageTableByCondition(ManageQueryVO vo);

    String addOneManage(ManageAddVO vo);

    String updateOneManage(ManageUpdateVO vo);

    String logicDeleteOneManage(Integer id);
}
