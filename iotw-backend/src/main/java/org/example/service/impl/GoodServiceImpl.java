package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.entity.dto.Good;
import org.example.entity.vo.request.GoodQueryVO;
import org.example.entity.vo.response.GoodTableVO;
import org.example.mapper.GoodMapper;
import org.example.service.GoodService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
        wrapper.select(Good::getGoodId, Good::getGoodName, Good::getWarehouseId, Good::getCategory,
                Good::getPrice, Good::getStandard, Good::getDescription, Good::getImage, Good::getCreateTime);

        // 3. 条件组合
        if (!StringUtils.isEmpty(vo.getGoodName())) {
            wrapper.like(Good::getGoodName, vo.getGoodName());
        }
        if (vo.getWarehouseId() != null) {
            wrapper.eq(Good::getWarehouseId, vo.getWarehouseId());
        }
        if (vo.getCategoryId() != null) {
            wrapper.eq(Good::getCategory, vo.getCategoryId());
        }
        if (vo.getStartPrice() != null && vo.getEndPrice() != null) {
            wrapper.between(Good::getPrice, vo.getStartPrice(), vo.getEndPrice());
        }
        if (vo.getStartPrice() != null && vo.getEndPrice() != null) {
            wrapper.between(Good::getPrice, vo.getStartPrice(), vo.getEndPrice());
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
                entity.getCategory(),
                entity.getPrice(),
                entity.getStandard(),
                entity.getDescription(),
                entity.getImage(),
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
    public String AddOneGood(Good good) {
        good.setCreateTime(LocalDateTime.now());

        return "";
    }

    private SFunction<Good, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "create_time" -> Good::getCreateTime;
            case "warehouse_id" -> Good::getWarehouseId;
            case "category" -> Good::getCategory;
            case "price" -> Good::getPrice;
            default -> Good::getGoodId;
        };
    }
}
