package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.dto.Supply;

/**
 * <p>
 * supply Mapper 接口
 * </p>
 *
 * @author hwshou
 * @since 2025-05-28 20:16
 */
@Mapper
public interface SupplyMapper extends BaseMapper<Supply> {

    Integer sumValidSupplyByGoodId(Integer goodId, Short status);
}

