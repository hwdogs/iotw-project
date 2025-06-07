package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.dto.Customer;
import org.example.entity.dto.Good;
import org.example.entity.dto.Sell;
import org.example.entity.vo.request.SellAddVO;
import org.example.entity.vo.request.SellQueryVO;
import org.example.entity.vo.request.SellUpdateVO;
import org.example.entity.vo.response.SellTableVO;
import org.example.mapper.CustomerMapper;
import org.example.mapper.GoodMapper;
import org.example.mapper.SellMapper;
import org.example.service.SellService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * sell出库服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Service
public class SellServiceImpl extends ServiceImpl<SellMapper, Sell> implements SellService {

    @Resource
    private CustomerMapper customerMapper;

    @Resource
    private GoodMapper goodMapper;

    /**
     * 请求出库记录表
     *
     * @param vo 请求信息
     * @return 请求结果
     */
    @Override
    public IPage<SellTableVO> querySellTableByCondition(SellQueryVO vo) {
        // 1. 构建分页对象
        Page<Sell> page = new Page<>(
                vo.getPageNum(),
                vo.getPageSize(),
                true);

        // 2. 构建动态查询条件
        LambdaQueryWrapper<Sell> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Sell::getSellId, Sell::getCustomerId, Sell::getGoodId,
                Sell::getSellNumber, Sell::getCreateTime, Sell::getUpdateTime);

        // 3. 条件组合
        if (vo.getSellId() != null) {
            wrapper.likeRight(Sell::getSellId, vo.getSellId());
        }
        if (vo.getCustomerId() != null) {
            wrapper.likeRight(Sell::getCustomerId, vo.getCustomerId());
        }
        if (vo.getGoodId() != null) {
            wrapper.likeRight(Sell::getGoodId, vo.getGoodId());
        }
        // 出库数量
        if (vo.getStartSellNumber() != null && vo.getEndSellNumber() == null) {
            wrapper.ge(Sell::getSellNumber, vo.getStartSellNumber());
        }
        if (vo.getStartSellNumber() == null && vo.getEndSellNumber() != null) {
            wrapper.le(Sell::getSellNumber, vo.getEndSellNumber());
        }
        if (vo.getStartSellNumber() != null && vo.getEndSellNumber() != null) {
            wrapper.between(Sell::getSellNumber, vo.getStartSellNumber(), vo.getEndSellNumber());
        }
        // 创建时间
        if (vo.getStartCreateTime() != null && vo.getEndCreateTime() == null) {
            wrapper.ge(Sell::getCreateTime, vo.getStartCreateTime());
        }
        if (vo.getStartCreateTime() == null && vo.getEndCreateTime() != null) {
            wrapper.le(Sell::getCreateTime, vo.getEndCreateTime());
        }
        if (vo.getStartCreateTime() != null && vo.getEndCreateTime() != null) {
            wrapper.between(Sell::getCreateTime, vo.getStartCreateTime(), vo.getEndCreateTime());
        }
        // 更新时间
        if (vo.getStartUpdateTime() != null && vo.getEndUpdateTime() == null) {
            wrapper.ge(Sell::getUpdateTime, vo.getStartUpdateTime());
        }
        if (vo.getStartUpdateTime() == null && vo.getEndUpdateTime() != null) {
            wrapper.le(Sell::getUpdateTime, vo.getEndUpdateTime());
        }
        if (vo.getStartUpdateTime() != null && vo.getEndUpdateTime() != null) {
            wrapper.between(Sell::getCreateTime, vo.getStartUpdateTime(), vo.getEndUpdateTime());
        }

        // 4.动态排序
        wrapper.orderBy(true, vo.getSortAsc(),
                getSortLambda(vo.getSortField()));

        // 5.转换为VO分页
        Page<Sell> sellPage = this.page(page, wrapper);

        return sellPage.convert(entity -> new SellTableVO(
                entity.getSellId(),
                entity.getCustomerId(),
                entity.getGoodId(),
                entity.getSellNumber(),
                entity.getCreateTime(),
                entity.getUpdateTime()
        ));
    }

    private SFunction<Sell, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "sell_id" -> Sell::getSellId;
            case "customer_id" -> Sell::getCustomerId;
            case "good_id" -> Sell::getGoodId;
            case "sell_number" -> Sell::getSellNumber;
            case "create_time" -> Sell::getCreateTime;
            default -> Sell::getUpdateTime;
        };
    }
}
