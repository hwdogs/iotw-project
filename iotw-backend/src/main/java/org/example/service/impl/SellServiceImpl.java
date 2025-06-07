package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.dto.Customer;
import org.example.entity.dto.Good;
import org.example.entity.dto.Sell;
import org.example.entity.dto.Supply;
import org.example.entity.vo.request.SellAddVO;
import org.example.entity.vo.request.SellQueryVO;
import org.example.entity.vo.request.SellUpdateVO;
import org.example.entity.vo.response.SellTableVO;
import org.example.mapper.CustomerMapper;
import org.example.mapper.GoodMapper;
import org.example.mapper.SellMapper;
import org.example.mapper.SupplyMapper;
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

    @Resource
    private SupplyMapper supplyMapper;

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
        if (StringUtils.isNotBlank(vo.getStartCreateTime()) && !StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.ge(Sell::getCreateTime, vo.getStartCreateTime());
        }
        if (!StringUtils.isNotBlank(vo.getStartCreateTime()) && StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.le(Sell::getCreateTime, vo.getEndCreateTime());
        }
        if (StringUtils.isNotBlank(vo.getStartCreateTime()) && StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.between(Sell::getCreateTime, vo.getStartCreateTime(), vo.getEndCreateTime());
        }
        // 更新时间
        if (StringUtils.isNotBlank(vo.getStartUpdateTime()) && !StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.ge(Sell::getUpdateTime, vo.getStartUpdateTime());
        }
        if (!StringUtils.isNotBlank(vo.getStartUpdateTime()) && StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.le(Sell::getUpdateTime, vo.getEndUpdateTime());
        }
        if (StringUtils.isNotBlank(vo.getStartUpdateTime()) && StringUtils.isNotBlank(vo.getEndUpdateTime())) {
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

    /**
     * 添加一条入库记录
     *
     * @param vo 进货信息
     * @return 是否入库成功
     */
    @Override
    public String addOneSell(SellAddVO vo) {
        // 1.参数校验
        if (vo.getCustomerId() == null) {
            return "顾客ID不能为空";
        }
        if (vo.getGoodId() == null) {
            return "货物ID不能为空";
        }

        /*
            不需要检查数据库中是否已存在将要添加的记录，
            同一个顾客可以进同一个货物多次
         */

        // 2.外键校验
        if (customerMapper.selectById(vo.getCustomerId()) == null) {
            return "顾客不存在";
        }

        Good sellGood = goodMapper.selectById(vo.getGoodId());
        if (sellGood == null) {
            return "货物不存在";
        }

        // 3.更新good表
        if (vo.getSellNumber() != null) {
            Integer allTheGoodNum = sellGood.getNum();
            Integer sellTheGoodNum = vo.getSellNumber();
            //如果要出库的数量大于库存数量，则返回
            if (sellTheGoodNum > allTheGoodNum) {
                return "当前商品数量小于" + sellTheGoodNum;
            }
            //否则更新对应good库存数量
            sellGood.setNum(allTheGoodNum - sellTheGoodNum);
            sellGood.setUpdateTime(LocalDateTime.now());
            goodMapper.updateById(sellGood);
        }

        // 4.安全转换
        Sell sell = vo.asDTO(Sell.class, target -> {
            target.setCreateTime(LocalDateTime.now());
            target.setUpdateTime(LocalDateTime.now());
        });

        return this.save(sell) ? null : "添加失败";
    }

    /**
     * 更新一条出库信息
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @Override
    public String updateOneSell(SellUpdateVO vo) {
        // 1.参数校验
        if (vo.getSellId() == null) {
            return "出库ID不能为空";
        }
        if (vo.getCustomerId() == null && vo.getGoodId() == null) {
            return "顾客和货物ID不能同时为空";
        }

        // 2.1 查询现有出库记录
        LambdaQueryWrapper<Sell> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sell::getSellId, vo.getSellId());
        if (this.count(wrapper) == 0) {
            return "更新的出库信息不存在";
        }

        // 2.2 如果记录存在则检查要更新的信息是否存在，包括被逻辑删除的
        LambdaQueryWrapper<Customer> customerWrapper = new LambdaQueryWrapper<>();
        customerWrapper.eq(Customer::getCustomerId, vo.getCustomerId())
                .last("OR deleted = 1");
        if (vo.getCustomerId() != null && customerMapper.selectOne(customerWrapper) == null) {
            return "更新的顾客不存在";
        }

        LambdaQueryWrapper<Good> goodWrapper = new LambdaQueryWrapper<>();
        goodWrapper.eq(Good::getGoodId, vo.getGoodId())
                .last("OR deleted = 1");
        ;
        if (vo.getGoodId() != null && goodMapper.selectOne(goodWrapper) == null) {
            return "更新的货物不存在";
        }

        // 3. 安全转换，只处理需要特殊处理的字段
        Sell sell = vo.asDTO(Sell.class, target -> {
            target.setUpdateTime(LocalDateTime.now());
        });

        return this.updateById(sell) ? null : "数据未变化";
    }

    /**
     * 逻辑删除一条出库记录
     *
     * @param sellId 入库记录id
     * @return 是否删除成功
     */
    @Override
    public String LogicDeleteOneSell(Integer sellId) {
        // 1.参数校验
        if (sellId == null) {
            return "出库ID不能为空";
        }

        // 2.查询现有出库记录
        LambdaQueryWrapper<Sell> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sell::getSellId, sellId);
        if (this.count(wrapper) == 0) {
            return "将要删除的出库记录不存在";
        }

        // 3.安全逻辑删除
        return this.removeById(sellId) ? null : "删除失败";
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
