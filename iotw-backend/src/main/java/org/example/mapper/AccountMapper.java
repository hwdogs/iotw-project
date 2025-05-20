package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.entity.dto.Account;

/**
 * @author hwshou
 * @date 2025/5/19  22:09
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
