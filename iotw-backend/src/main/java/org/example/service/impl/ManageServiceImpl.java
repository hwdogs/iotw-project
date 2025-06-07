package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.entity.dto.Manage;
import org.example.entity.dto.Sell;
import org.example.entity.vo.request.ManageAddVO;
import org.example.entity.vo.request.ManageQueryVO;
import org.example.entity.vo.response.ManageTableVO;
import org.example.entity.vo.request.ManageUpdateVO;
import org.example.mapper.ManageMapper;
import org.example.service.ManageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * manage服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-31 14:51
 */
@Service
public class ManageServiceImpl extends ServiceImpl<ManageMapper, Manage> implements ManageService {

    /**
     * 管理界面多条件分页查询，支持排序
     *
     * @param vo 查询条件请求vo
     * @return 返回查询结果响应vo
     */
    @Override
    public IPage<ManageTableVO> queryManageTableByCondition(ManageQueryVO vo) {
        // 1.构建分页对象
        Page<Manage> page = new Page<>(
                vo.getPageNum(),
                vo.getPageSize(),
                true);

        // 2.构建动态查询条件
        LambdaQueryWrapper<Manage> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Manage::getManageId, Manage::getWarehouseId,
                Manage::getAccountId, Manage::getCreateTime, Manage::getUpdateTime);

        // 3. 条件组合
        if (vo.getWarehouseId() != null) {
            wrapper.eq(Manage::getWarehouseId, vo.getWarehouseId());
        }
        if (vo.getAccountId() != null) {
            wrapper.eq(Manage::getAccountId, vo.getAccountId());
        }

        if (StringUtils.isNotBlank(vo.getStartCreateTime()) && !StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.ge(Manage::getCreateTime, vo.getStartCreateTime());
        }
        if (!StringUtils.isNotBlank(vo.getStartCreateTime()) && StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.le(Manage::getCreateTime, vo.getEndCreateTime());
        }
        if (StringUtils.isNotBlank(vo.getStartCreateTime()) && StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.between(Manage::getCreateTime, vo.getStartCreateTime(), vo.getEndCreateTime());
        }
        // 更新时间
        if (StringUtils.isNotBlank(vo.getStartUpdateTime()) && !StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.ge(Manage::getUpdateTime, vo.getStartUpdateTime());
        }
        if (!StringUtils.isNotBlank(vo.getStartUpdateTime()) && StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.le(Manage::getUpdateTime, vo.getEndUpdateTime());
        }
        if (StringUtils.isNotBlank(vo.getStartUpdateTime()) && StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.between(Manage::getCreateTime, vo.getStartUpdateTime(), vo.getEndUpdateTime());
        }

        // 4.动态排序
        wrapper.orderBy(true, vo.getSortAsc(),
                getSortLambda(vo.getSortField()));

        // 5.转化为VO分页
        Page<Manage> managePage = this.page(page, wrapper);

        return managePage.convert(entity -> new ManageTableVO(
                entity.getManageId(),
                entity.getWarehouseId(),
                entity.getAccountId(),
                entity.getCreateTime(),
                entity.getUpdateTime()));
    }

    /**
     * 添加一条管理记录
     *
     * @param vo 管理信息
     * @return 是否管理成功
     */
    @Override
    public String addOneManage(ManageAddVO vo) {
        if (vo.getAccountId() == null) {
            return "管理员账户ID不能为空";
        }
        if (vo.getWarehouseId() == null) {
            return "仓库ID不能为空";
        }

        // 检查数据库中是否已存在将要添加的记录
        // 检查唯一性约束
        LambdaQueryWrapper<Manage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Manage::getWarehouseId, vo.getWarehouseId())
                .eq(Manage::getAccountId, vo.getAccountId());
        if (this.count(queryWrapper) > 0) {
            return "该仓库管理员已存在，请勿重复添加"; // 返回友好提示
        }

        Manage manage = vo.asDTO(Manage.class, target -> {
            target.setCreateTime(LocalDateTime.now());
            target.setUpdateTime(LocalDateTime.now());
        });

        this.save(manage);

        return null;
    }

    /**
     * 更新一条管理记录
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @Override
    public String updateOneManage(ManageUpdateVO vo) {
        // 1.参数校验
        if (vo.getManageId() == null) {
            return "id不能为空";
        }
        if (vo.getWarehouseId() == null && vo.getAccountId() == null) {
            return "传入的更新数据为空";
        }

        // 2.1 查询现有账户
        LambdaQueryWrapper<Manage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Manage::getManageId, vo.getManageId());
        if (this.count(queryWrapper) == 0) {
            return "更新的管理记录不存在";
        }

        // 2.2 检查是否与其他记录冲突（排除自身）
        LambdaQueryWrapper<Manage> conflictCheck = new LambdaQueryWrapper<>();
        conflictCheck.eq(Manage::getWarehouseId, vo.getWarehouseId())
                .eq(Manage::getAccountId, vo.getAccountId())
                .ne(Manage::getManageId, vo.getManageId()); // 排除当前记录
        if (this.count(conflictCheck) > 0) {
            return "该仓库-账户组合已存在，无法更新";
        }

        // 3. 安全转换，只处理需要特殊处理的字段
        Manage updateManage = vo.asDTO(Manage.class, target -> target.setUpdateTime(LocalDateTime.now()));

        return this.updateById(updateManage) ? null : "数据未变化";
    }

    /**
     * 逻辑删除一条管理记录
     *
     * @param id 需要删除记录的id
     * @return 是否删除成功
     */
    @Override
    public String logicDeleteOneManage(Integer id) {
        if (id == null) {
            return "id不能为空";
        }
        return this.removeById(id) ? null : "删除失败";

    }

    private SFunction<Manage, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "manage_id" -> Manage::getManageId;
            case "warehouse_id" -> Manage::getWarehouseId;
            case "account_id" -> Manage::getAccountId;
            case "create_time" -> Manage::getCreateTime;
            default -> Manage::getUpdateTime;
        };
    }
}
