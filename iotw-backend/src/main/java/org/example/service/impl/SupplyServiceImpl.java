package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.entity.dto.Good;
import org.example.entity.dto.Supply;
import org.example.entity.vo.request.SupplyAddVO;
import org.example.entity.vo.request.SupplyQueryVO;
import org.example.entity.vo.request.SupplyUpdateVO;
import org.example.entity.vo.response.SupplyTableVO;
import org.example.mapper.GoodMapper;
import org.example.mapper.SupplierMapper;
import org.example.mapper.SupplyMapper;
import org.example.service.SupplyService;
import org.example.utils.Const;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * supply入库服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Service
public class SupplyServiceImpl extends ServiceImpl<SupplyMapper, Supply> implements SupplyService {

    @Resource
    private SupplyMapper supplyMapper;

    @Resource
    private SupplierMapper supplierMapper;

    @Resource
    private GoodMapper goodMapper;

    /**
     * 请求入库记录列表
     *
     * @param vo 请求信息
     * @return 请求结果
     */
    @Override
    public IPage<SupplyTableVO> querySupplyTableByCondition(SupplyQueryVO vo) {
        // 1.构建分页对象
        Page<Supply> page = new Page<>(
                vo.getPageNum(),
                vo.getPageSize(),
                true);

        // 2.构建动态查询条件
        LambdaQueryWrapper<Supply> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Supply::getSupplyId, Supply::getSupplierId, Supply::getGoodId, Supply::getSupplyNumber,
                Supply::getStatus, Supply::getCreateTime, Supply::getUpdateTime);

        // 3. 条件组合
        if (vo.getSupplyId() != null) {
            wrapper.likeRight(Supply::getSupplyId, vo.getSupplyId());
        }
        if (vo.getGoodId() != null) {
            wrapper.likeRight(Supply::getGoodId, vo.getGoodId());
        }
        if (vo.getSupplierId() != null) {
            wrapper.likeRight(Supply::getSupplierId, vo.getSupplierId());
        }
        if (vo.getStatus() != null) {
            wrapper.eq(Supply::getStatus, vo.getStatus());
        }
        // 货物数量
        // [100, ...)
        if (vo.getStartSupplyNumber() != null && vo.getEndSupplyNumber() == null) {
            wrapper.ge(Supply::getSupplyNumber, vo.getStartSupplyNumber());
        }
        // (..., 200]
        if (vo.getStartSupplyNumber() == null && vo.getEndSupplyNumber() != null) {
            wrapper.le(Supply::getSupplyNumber, vo.getEndSupplyNumber());
        }
        // [100, 200]
        if (vo.getStartSupplyNumber() != null && vo.getEndSupplyNumber() != null) {
            wrapper.between(Supply::getSupplyNumber, vo.getStartSupplyNumber(), vo.getEndSupplyNumber());
        }
        // 创建时间
        if (StringUtils.isNotBlank(vo.getStartCreateTime()) && !StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.ge(Supply::getCreateTime, vo.getStartCreateTime());
        }
        if (!StringUtils.isNotBlank(vo.getStartCreateTime()) && StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.le(Supply::getCreateTime, vo.getEndCreateTime());
        }
        if (StringUtils.isNotBlank(vo.getStartCreateTime()) && StringUtils.isNotBlank(vo.getEndCreateTime())) {
            wrapper.between(Supply::getCreateTime, vo.getStartCreateTime(), vo.getEndCreateTime());
        }
        // 更新时间
        if (StringUtils.isNotBlank(vo.getStartUpdateTime()) && !StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.ge(Supply::getUpdateTime, vo.getStartUpdateTime());
        }
        if (!StringUtils.isNotBlank(vo.getStartUpdateTime()) && StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.le(Supply::getUpdateTime, vo.getEndUpdateTime());
        }
        if (StringUtils.isNotBlank(vo.getStartUpdateTime()) && StringUtils.isNotBlank(vo.getEndUpdateTime())) {
            wrapper.between(Supply::getCreateTime, vo.getStartUpdateTime(), vo.getEndUpdateTime());
        }

        // 4.动态排序
        wrapper.orderBy(true, vo.getSortAsc(),
                getSortLambda(vo.getSortField()));

        // 5.转化为VO分页
        Page<Supply> supplyPage = this.page(page, wrapper);

        return supplyPage.convert(entity -> new SupplyTableVO(
                entity.getSupplyId(),
                entity.getSupplierId(),
                entity.getGoodId(),
                entity.getSupplyNumber(),
                entity.getStatus(),
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
    public String addOneSupply(SupplyAddVO vo) {
        // 1. 参数校验
        if (vo.getSupplierId() == null) {
            return "供货商ID不能为空";
        }
        if (vo.getGoodId() == null) {
            return "货物ID不能为空";
        }

        /*
            不需要检查数据库中是否已存在将要添加的记录，
            同一个供货商可以进同一个货物不同时多次
         */

        // 2。 外键校验
        if (supplierMapper.selectById(vo.getSupplierId()) == null) {
            return "供货商不存在";
        }
        if (goodMapper.selectById(vo.getGoodId()) == null) {
            return "货物不存在";
        }

        // 3. 安全转换
        Supply supply = vo.asDTO(Supply.class, target -> {
            target.setStatus(Const.NOT_APPROVED);
            target.setCreateTime(LocalDateTime.now());
            target.setUpdateTime(LocalDateTime.now());
        });

        return this.save(supply) ? null : "添加失败";
    }

    /**
     * 更新一条入库信息
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @Override
    public String updateOneSupply(SupplyUpdateVO vo) {
        // 1.参数校验
        if (vo.getSupplyId() == null) {
            return "入库ID不能为空";
        }
        if (vo.getSupplierId() == null) {
            return "供货商ID不能为空";
        }
        if (vo.getGoodId() == null) {
            return "货物ID不能为空";
        }
        if (vo.getStatus() == null) {
            return "货物状态不能为空!!!";
        }

        // 2 获取原始入库记录
        Supply oldSupply = supplyMapper.selectById(vo.getSupplyId());
        if (oldSupply == null) {
            return "更新的入库记录不存在";
        }

        // 3. 检查供应商和货物是否存在
        if (!vo.getSupplierId().equals(oldSupply.getSupplierId()) &&
                supplierMapper.selectById(vo.getSupplierId()) == null) {
            return "更新的供货商不存在";
        }

        Good newGood = goodMapper.selectById(vo.getGoodId());
        if (newGood == null) return "更新的货物不存在";

        // 4. 处理 GoodId 变更
        Good oldGood = null;
        if (!vo.getGoodId().equals(oldSupply.getGoodId())) {
            oldGood = goodMapper.selectById(oldSupply.getGoodId());
            if (oldGood == null) return "原始货物不存在";
        }

        // 记录受影响商品ID集合
        Set<Integer> affectedGoodIds = new HashSet<>();
        affectedGoodIds.add(oldSupply.getGoodId()); // 原商品ID
        affectedGoodIds.add(vo.getGoodId());        // 新商品ID

        // 更新t_supply记录
        Supply newSupply = vo.asDTO(Supply.class, target ->
                target.setUpdateTime(LocalDateTime.now())
        );
        if (!this.updateById(newSupply)) return "数据未变化";

        // 重算所有受影响商品的库存
        for (Integer goodId : affectedGoodIds) {
            recalculateGoodInventory(goodId);
        }
        return null;
        
//        // 2.3 判断status变化情况
//        Good good = goodMapper.selectById(vo.getGoodId());
//        Short oldStatus = supplyMapper.selectById(vo.getSupplyId()).getStatus();
//        Short newStata = vo.getStatus();
//
//        /* 之前为0，现在为0，不变 */
//        /* 之前为0，现在为1，说明status本身不为1然后要设置为1，说明管理员把待审核的审核通过了，则级联更新good表的num */
//        if (Objects.equals(oldStatus, Const.NOT_APPROVED) && Objects.equals(newStata, Const.APPROVED_SUCCESSFULLY)) {
//            Integer oldNum = good.getNum();                    //原先的旧数量，若是第一次入库则为0
//            good.setNum(oldNum + vo.getSupplyNumber());        //原先的数量+要入库的数量
//            good.setUpdateTime(LocalDateTime.now());
//            goodMapper.updateById(good);
//        }
//        /* 之前为0，现在为-1，不变 */
//        /* 之前为1，现在也为1 ，如果state前后都为1, 说明入库数量之前可能有错误，则先good num先删去supply的旧数据，在加上新数据 */
//        if (Objects.equals(oldStatus, Const.APPROVED_SUCCESSFULLY) && Objects.equals(newStata, Const.APPROVED_SUCCESSFULLY)) {
//            Integer oldGoodNum = goodMapper.selectById(vo.getGoodId()).getNum();
//            Integer oldSupplyNum = supplyMapper.selectById(vo.getSupplyId()).getSupplyNumber();
//            Integer newSupplyNum = vo.getSupplyNumber();
//
//            int finalNum = oldGoodNum - oldSupplyNum + newSupplyNum;
//            if (finalNum < 0) return "货物已售出，不能更改";
//            good.setNum(finalNum);              //原先的数量-本入库单之前入库的数据+现在要入库的数据
//            good.setUpdateTime(LocalDateTime.now());
//            goodMapper.updateById(good);
//        }
//        /* 之前为1，现在为0，说明管理员把审批通过的的变为未通过的，则good num减去这个入库单里面的supply num */
//        /* 之前为1，现在为-1，说明管理员把审批通过的变成审批不通过的，则good num减去这个入库单里面的supply num */
//        if (Objects.equals(oldStatus, Const.APPROVED_SUCCESSFULLY) && Objects.equals(newStata, Const.NOT_APPROVED)
//                || Objects.equals(oldStatus, Const.APPROVED_SUCCESSFULLY) && Objects.equals(newStata, Const.APPROVED_UNSUCCESSFULLY)) {
//            Integer oldGoodNum = goodMapper.selectById(vo.getGoodId()).getNum();
//            Integer alreadySupplyNum = vo.getSupplyNumber();
//
//            int finalNum = oldGoodNum - alreadySupplyNum;
//            if (finalNum < 0) return "货物已售出，不能更改";
//            good.setNum(finalNum);
//            good.setUpdateTime(LocalDateTime.now());
//            goodMapper.updateById(good);
//        }
//        /* 之前为-1，现在为1，说明之前审批不通过的被审批通过了，则good num加上审批通过的入库单的supply num */
//        if (Objects.equals(oldStatus, Const.APPROVED_UNSUCCESSFULLY) && Objects.equals(newStata, Const.APPROVED_SUCCESSFULLY)) {
//            Integer oldGoodNum = goodMapper.selectById(vo.getGoodId()).getNum();
//            Integer newSupplyNum = vo.getSupplyNumber();
//
//            int finalNum = oldGoodNum + newSupplyNum;
//            good.setNum(finalNum);
//            good.setUpdateTime(LocalDateTime.now());
//            goodMapper.updateById(good);
//        }
//
//        /* 之前为-1，现在为0，不变 */
//        /* 之前为-1，现在为-1，不变 */
//
//        // 3. 安全转换，只处理需要特殊处理的字段
//        Supply newsupply = vo.asDTO(Supply.class, target -> {
//            target.setUpdateTime(LocalDateTime.now());
//            if (vo.getStatus() != null) {
//                target.setStatus(vo.getStatus());
//            }
//        });
//
//        return this.updateById(newsupply) ? null : "数据未变化";
    }

    private void recalculateGoodInventory(Integer goodId) {
        // 计算有效入库总量 (status=1且未删除)
        Integer totalNum = supplyMapper.sumValidSupplyByGoodId(goodId, Const.APPROVED_SUCCESSFULLY);
        if (totalNum == null) totalNum = 0;

        // 更新商品库存
        Good good = goodMapper.selectById(goodId);
        good.setNum(totalNum);
        good.setUpdateTime(LocalDateTime.now());
        goodMapper.updateById(good);
    }

    /**
     * 逻辑删除一条入库记录
     *
     * @param supplyId 入库记录id
     * @return 是否删除成功
     */
    @Override
    public String logicDeleteOneSupply(Integer supplyId) {
        // 1.参数校验
        if (supplyId == null) {
            return "入库ID不能为空";
        }

        // 2.查询现有入库记录
        LambdaQueryWrapper<Supply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Supply::getSupplyId, supplyId);
        if (this.count(queryWrapper) == 0) {
            return "将要删除的入库记录不存在";
        }

        // 3.安全逻辑删除
        return this.removeById(supplyId) ? null : "删除失败";
    }


    private SFunction<Supply, ?> getSortLambda(String sortField) {
        return switch (sortField) {
            case "supply_id" -> Supply::getSupplyId;
            case "supply_number" -> Supply::getSupplyNumber;
            case "supplier_id" -> Supply::getSupplierId;
            case "good_id" -> Supply::getGoodId;
            case "create_time" -> Supply::getCreateTime;
            default -> Supply::getUpdateTime;
        };
    }
}
