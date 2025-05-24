package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Account;
import org.example.entity.vo.EmailRegisterVO;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author hwshou
 * @date 2025/5/19  22:10
 */
public interface AccountService extends IService<Account> , UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    String registerEmailVerifyCode(String type,String email, String ip);
    String registerEmailAccount(EmailRegisterVO vo);
}
