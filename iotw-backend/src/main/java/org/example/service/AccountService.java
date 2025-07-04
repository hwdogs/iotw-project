package org.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.dto.Account;
import org.example.entity.vo.request.*;
import org.example.entity.vo.response.AccountIdUsernameVO;
import org.example.entity.vo.response.AccountTableOV;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * @author hwshou
 * @date 2025/5/19  22:10
 */
public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);

    String registerEmailVerifyCode(String type, String email, String ip);

    String registerEmailAccount(AccountEmailRegisterVO vo);

    String resetConfirm(ConfirmResetVO vo);

    String resetEmailAccountPassword(AccountEmailRestVO vo);

    IPage<AccountTableOV> queryByConditions(AccountQueryVO vo);

    String logicDeleteOneAccountRecord(Integer id);

    String updateOneAccount(AccountUpdateVO vo);

    String addOneAccount(AccountAddVO vo);

    List<AccountIdUsernameVO> getAllAccountIdsAndUsernames();

    AccountTableOV getCurrentAccountInfo();
}
