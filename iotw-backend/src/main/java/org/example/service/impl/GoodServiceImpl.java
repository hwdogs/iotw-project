package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.dto.Good;
import org.example.entity.vo.request.GoodAddVO;
import org.example.entity.vo.request.GoodQueryVO;
import org.example.entity.vo.request.GoodUpdateVO;
import org.example.entity.vo.response.GoodTableVO;
import org.example.mapper.GoodMapper;
import org.example.mapper.WarehouseMapper;
import org.example.service.GoodService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Service
public class GoodServiceImpl extends ServiceImpl<GoodMapper, Good> implements GoodService {

    @Resource
    private GoodMapper goodMapper;

    @Resource
    private WarehouseMapper warehouseMapper;

    /**
     * 货物多条件分页查询，支持排序
     *
     * @param vo 查询条件请求vo
     * @return 返回查询结果响应vo
     */
    @Override
    public IPage<GoodTableVO> queryGoodTableByConditions(GoodQueryVO vo) {
        // 1.构建分页对象
        Page<Good> page = new Page<>(
                vo.getPageNum(),
                vo.getPageSize(),
                true);

        // 2.构建动态查询条件
        LambdaQueryWrapper<Good> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Good::getGoodId, Good::getGoodName, Good::getWarehouseId,
                Good::getNum, Good::getCategory, Good::getPrice, Good::getStandard,
                Good::getDescription, Good::getImage, Good::getUpdateTime, Good::getCreateTime);

        // 3. 条件组合
        if (vo.getGoodId() != null) {
            wrapper.likeRight(Good::getGoodId, vo.getGoodId());
        }
        if (!StringUtils.isEmpty(vo.getGoodName())) {
            wrapper.like(Good::getGoodName, vo.getGoodName());
        }
        if (vo.getWarehouseId() != null) {
            wrapper.eq(Good::getWarehouseId, vo.getWarehouseId());
        }
        if (vo.getCategoryId() != null) {
            wrapper.eq(Good::getCategory, vo.getCategoryId());
        }

        if (vo.getStartNum() != null && vo.getEndNum() == null) {
            wrapper.ge(Good::getPrice, vo.getStartNum());
        }
        if (vo.getStartNum() == null && vo.getEndNum() != null) {
            wrapper.le(Good::getPrice, vo.getEndNum());
        }
        if (vo.getStartNum() != null && vo.getEndNum() != null) {
            wrapper.between(Good::getPrice, vo.getStartNum(), vo.getEndNum());
        }

        if (vo.getStartPrice() != null && vo.getEndPrice() == null) {
            wrapper.ge(Good::getPrice, vo.getStartPrice());
        }
        if (vo.getStartPrice() == null && vo.getEndPrice() != null) {
            wrapper.le(Good::getPrice, vo.getEndPrice());
        }
        if (vo.getStartPrice() != null && vo.getEndPrice() != null) {
            wrapper.between(Good::getPrice, vo.getStartPrice(), vo.getEndPrice());
        }

        // 更新时间
        if (StringUtils.isNotBlank(vo.getStartUpdateTime()) && !StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.ge(Good::getUpdateTime, vo.getStartUpdateTime());
        }
        if (!StringUtils.isNotBlank(vo.getStartUpdateTime()) && StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.le(Good::getUpdateTime, vo.getEndUpdateTime());
        }
        if (StringUtils.isNotBlank(vo.getStartUpdateTime()) && StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.between(Good::getUpdateTime, vo.getStartUpdateTime(), vo.getEndUpdateTime());
        }

        if (StringUtils.isNotBlank(vo.getStartCreateTime()) && !StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.ge(Good::getCreateTime, vo.getStartCreateTime());
        }
        if (!StringUtils.isNotBlank(vo.getStartCreateTime()) && StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.le(Good::getCreateTime, vo.getEndCreateTime());
        }
        if (StringUtils.isNotBlank(vo.getStartCreateTime()) && StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.between(Good::getCreateTime, vo.getStartCreateTime(), vo.getEndCreateTime());
        }

        // 4.动态排序
        wrapper.orderBy(true, vo.getSortAsc(),
                getSortLambda(vo.getSortField()));

        // 5.转化为VO分页
        Page<Good> goodPage = goodMapper.selectPage(page, wrapper);

        return goodPage.convert(entity -> new GoodTableVO(
                entity.getGoodId(),
                entity.getGoodName(),
                entity.getWarehouseId(),
                entity.getNum(),
                entity.getCategory(),
                entity.getPrice(),
                entity.getStandard(),
                entity.getDescription(),
                entity.getImage(),
                entity.getUpdateTime(),
                entity.getCreateTime()));
    }

    @Override
    public String logicDeleteOneGood(Integer id) {
        int deleted = goodMapper.deleteById(id);
        if (deleted != 0) {
            return null;
        }
        return "删除失败";
    }

    @Override
    public String AddOneGood(GoodAddVO vo) {
        Good good = vo.asDTO(Good.class, target -> {
            target.setUpdateTime(LocalDateTime.now());
            target.setCreateTime(LocalDateTime.now());
        });

        if (vo.getWarehouseId() != null && warehouseMapper.selectById(vo.getWarehouseId()) == null) {
            return "入库的仓库不存在";
        }

        return this.goodMapper.insert(good) > 0 ? null : "添加失败";
    }

    /**
     * 更新一个货物信息
     *
     * @param vo 更新的信息
     * @return 是否更新成功
     */
    @Override
    public String updateOneGood(GoodUpdateVO vo) {
        if (vo.getGoodId() == null) {
            return "货物id不能为空";
        }
        if (vo.getWarehouseId() != null && warehouseMapper.selectById(vo.getWarehouseId()) == null) {
            return "仓库不存在";
        }

        Good good = vo.asDTO(Good.class, target -> target.setUpdateTime(LocalDateTime.now()));

        return goodMapper.updateById(good) > 0 ? null : "数据未变化";
    }

    private SFunction<Good, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "create_time" -> Good::getCreateTime;
            case "warehouse_id" -> Good::getWarehouseId;
            case "category" -> Good::getCategory;
            case "price" -> Good::getPrice;
            case "num" -> Good::getNum;
            default -> Good::getUpdateTime;
        };
    }
}
