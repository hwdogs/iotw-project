package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.AccountQueryVO;
import org.example.entity.vo.request.ConfirmResetVO;
import org.example.entity.vo.request.EmailRegisterVO;
import org.example.entity.vo.request.EmailRestVO;
import org.example.entity.vo.response.AccountTableOV;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @author hwshou
 * @date 2025/5/19  22:10
 */
public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);

    String registerEmailVerifyCode(String type, String email, String ip);

    String registerEmailAccount(EmailRegisterVO vo);

    String resetConfirm(ConfirmResetVO vo);

    String resetEmailAccountPassword(EmailRestVO vo);

    IPage<AccountTableOV> queryByConditions(AccountQueryVO vo);
}
