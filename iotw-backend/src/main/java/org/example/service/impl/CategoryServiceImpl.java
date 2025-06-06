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

    /**
     * 添加一个类别
     *
     * @param vo 添加信息
     * @return 是否添加成功
     */
    @Override
    public String addOneCategory(CategoryAddVO vo) {
        if (vo.getCategoryId() == null) {
            return "类别ID不能为空";
        }
        if (StringUtils.isBlank(vo.getCategoryName())) {
            return "类别名称不能为空";
        }

        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getCategoryName, vo.getCategoryName())
                .eq(Category::getCategoryId, vo.getCategoryId());

        if (this.count(wrapper) > 0) {
            return "该类别已存在，请勿重复添加";
        }

        Category category = vo.asDTO(Category.class);

        return this.save(category) ? null : "存入失败";
    }

    /**
     * 更新一条类别记录
     *
     * @param vo 需要更新的信息
     * @return 是否更新成功
     */
    @Override
    public String updateOneCategory(CategoryUpdateVO vo) {
        // 1.参数校验
        if (vo.getCategoryId() == null) {
            return "id不能为空";
        }
        if (StringUtils.isBlank(vo.getCategoryName())) {
            return "传入的更新数据为空";
        }

        // 2.1 查询现有账户
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getCategoryId, vo.getCategoryId());
        if (this.count(wrapper) == 0) {
            return "更新的类别记录不存在";
        }

        // 2.2 检查是否与其他记录冲突（排除自身）
        LambdaQueryWrapper<Category> conflictCheck = new LambdaQueryWrapper<>();
        conflictCheck.eq(Category::getCategoryName, vo.getCategoryName())
                .ne(Category::getCategoryId, vo.getCategoryId());
        if (this.count(conflictCheck) > 0) {
            return "该类别已存在，无法更新";
        }

        // 3. 安全转换，只处理需要特殊处理的字段
        Category category = vo.asDTO(Category.class);

        return this.updateById(category) ? null : "数据为变化";
    }

    /**
     * 逻辑删除一条类别记录
     *
     * @param categoryId 需要删除记录的id
     * @return 是否删除成功
     */
    @Override
    public String logicDeleteOneCategory(Short categoryId) {
        if (categoryId == null) {
            return "id不能为空";
        }
        return this.removeById(categoryId) ? null : "删除失败";
    }
}
