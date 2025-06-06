package org.example.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 通用分页查询工具类
 *
 * @author hwshou
 * @date 2025/6/6  13:37
 */
public class PageQueryUtils {
    /**
     * 通用分页查询方法
     *
     * @param vo               查询条件VO
     * @param pageNumGetter    获取pageNum的函数
     * @param pageSizeGetter   获取pageSize的函数
     * @param service          服务接口
     * @param fieldSelector    字段选择函数
     * @param conditionBuilder 条件构建函数
     * @param voConverter      实体转VO转换器
     * @param <T>              实体类型
     * @param <V>              查询VO类型
     * @param <R>              结果VO类型
     * @return 分页查询结果
     */
    public static <T, V, R> IPage<R> queryByConditions(
            V vo,
            Function<V, Long> pageNumGetter,
            Function<V, Long> pageSizeGetter,
            IService<T> service,
            Function<LambdaQueryWrapper<T>, LambdaQueryWrapper<T>> fieldSelector,
            BiConsumer<V, LambdaQueryWrapper<T>> conditionBuilder,
            Function<T, R> voConverter) {

        // 1.构建分页对象
        Page<T> page = new Page<>(
                pageNumGetter.apply(vo),
                pageSizeGetter.apply(vo),
                true
        );

        // 2.构建动态查询条件
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper = fieldSelector.apply(wrapper);

        // 3.应用查询条件
        conditionBuilder.accept(vo, wrapper);

        // 4.执行查询并转换结果
        return service.page(page, wrapper).convert(voConverter);
    }

    /**
     * 通用条件构建器
     */
    public static <T, V> void buildCommonConditions(
            V vo,
            LambdaQueryWrapper<T> wrapper,
            // 值获取函数
            Function<V, Integer> idGetter,
            Function<V, String> usernameGetter,
            Function<V, String> emailGetter,
            Function<V, String> phoneGetter,
            Function<V, String> addressGetter,
            Function<V, Short> sexGetter,
            Function<V, LocalDate> startBirthGetter,
            Function<V, LocalDate> endBirthGetter,
            Function<V, LocalDateTime> startCreateTimeGetter,
            Function<V, LocalDateTime> endCreateTimeGetter,
            Function<V, LocalDateTime> startUpdateTimeGetter,
            Function<V, LocalDateTime> endUpdateTimeGetter,
            // 字段获取函数
            SFunction<T, ?> idField,
            SFunction<T, ?> usernameField,
            SFunction<T, ?> emailField,
            SFunction<T, ?> phoneField,
            SFunction<T, ?> addressField,
            SFunction<T, ?> sexField,
            SFunction<T, ?> birthField,
            SFunction<T, ?> createTimeField,
            SFunction<T, ?> updateTimeField,
            // 排序相关函数
            Function<V, String> sortFieldGetter,
            Function<V, Boolean> sortAscGetter,
            Function<String, SFunction<T, ?>> sortFunction) {

        // ID条件
        if (idGetter.apply(vo) != null) {
            wrapper.likeRight(idField, idGetter.apply(vo));
        }

        // 用户名条件
        if (StringUtils.isNotBlank(usernameGetter.apply(vo))) {
            wrapper.like(usernameField, usernameGetter.apply(vo));
        }

        // 邮箱条件
        if (StringUtils.isNotBlank(emailGetter.apply(vo))) {
            wrapper.eq(emailField, emailGetter.apply(vo));
        }

        // 电话条件
        if (StringUtils.isNotBlank(phoneGetter.apply(vo))) {
            wrapper.likeRight(phoneField, phoneGetter.apply(vo));
        }

        // 地址条件
        if (StringUtils.isNotBlank(addressGetter.apply(vo))) {
            wrapper.like(addressField, addressGetter.apply(vo));
        }

        // 性别条件
        if (sexGetter.apply(vo) != null) {
            wrapper.eq(sexField, sexGetter.apply(vo));
        }

        // 生日范围
        LocalDate startBirth = startBirthGetter.apply(vo);
        LocalDate endBirth = endBirthGetter.apply(vo);
        if (startBirth != null && endBirth != null) {
            wrapper.between(birthField, startBirth, endBirth);
        }

        // 创建时间范围
        LocalDateTime startCreateTime = startCreateTimeGetter.apply(vo);
        LocalDateTime endCreateTime = endCreateTimeGetter.apply(vo);
        if (startCreateTime != null && endCreateTime != null) {
            wrapper.between(createTimeField, startCreateTime, endCreateTime);
        }

        // 更新时间范围
        LocalDateTime startUpdateTime = startUpdateTimeGetter.apply(vo);
        LocalDateTime endUpdateTime = endUpdateTimeGetter.apply(vo);
        if (startUpdateTime != null && endUpdateTime != null) {
            wrapper.between(updateTimeField, startUpdateTime, endUpdateTime);
        }

        // 排序
        wrapper.orderBy(true, sortAscGetter.apply(vo),
                sortFunction.apply(sortFieldGetter.apply(vo)));
    }
}
