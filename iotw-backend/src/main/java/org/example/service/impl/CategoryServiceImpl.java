package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.dto.Category;
import org.example.entity.vo.request.CategoryAddVO;
import org.example.entity.vo.request.CategoryQueryVO;
import org.example.entity.vo.request.CategoryUpdateVO;
import org.example.entity.vo.response.CategoryTableVO;
import org.example.mapper.CategoryMapper;
import org.example.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * category服务实现类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    /**
     * 类别多条件分页查询
     *
     * @param vo 查询条件
     * @return 查询结果
     */
    @Override
    public IPage<CategoryTableVO> queryCategoryTableByCondition(CategoryQueryVO vo) {
        // 1.构建分页对象
        Page<Category> page = new Page<>(
                vo.getPageNum(),
                vo.getPageSize(),
                true
        );

        // 2.构建动态查询条件
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(Category::getCategoryId, Category::getCategoryName);

        // 3. 条件组合
        if (vo.getCategoryId() != null) {
            wrapper.likeRight(Category::getCategoryId, vo.getCategoryId());
        }
        if (StringUtils.isNotBlank(vo.getCategoryName())) {
            wrapper.like(Category::getCategoryName, vo.getCategoryName());
        }

        // 4.动态排序
        wrapper.orderBy(true, vo.getSortAsc(), Category::getCategoryId);

        // 5.转化为VO分页
        Page<Category> categoryPage = this.page(page, wrapper);

        return categoryPage.convert(entity -> new CategoryTableVO(
                entity.getCategoryId(),
                entity.getCategoryName()
        ));
    }
}
