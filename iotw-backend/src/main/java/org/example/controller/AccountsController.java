package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.vo.request.AccountQueryVO;
import org.example.entity.vo.response.AccountTableOV;
import org.example.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 处理总体账户操作
 *
 * @author hwshou
 * @date 2025/5/27  12:55
 */
@RestController
@RequestMapping("/api/account")
public class AccountsController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/query")
    public RestBean<IPage<AccountTableOV>> queryAccounts(
            @Valid @RequestBody AccountQueryVO vo) {
        return RestBean.success(accountService.queryByConditions(vo));
    }
}
