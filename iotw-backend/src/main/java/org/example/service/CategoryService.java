package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Category;
import org.example.entity.vo.request.CategoryAddVO;
import org.example.entity.vo.request.CategoryQueryVO;
import org.example.entity.vo.request.CategoryUpdateVO;
import org.example.entity.vo.response.CategoryTableVO;

/**
 * <p>
 * category服务类
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
public interface CategoryService extends IService<Category> {
    IPage<CategoryTableVO> queryCategoryTableByCondition(CategoryQueryVO vo);


}
