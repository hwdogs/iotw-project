package org.example.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import org.example.entity.RestBean;
import org.example.entity.vo.request.AccountQueryVO;
import org.example.entity.vo.request.AccountUpdateVO;
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

    @Resource
    private AccountService accountService;

    /**
     * 请求账户信息
     *
     * @param vo 请求信息
     * @return 响应对应vo类
     */
    @PostMapping("/query")
    public RestBean<IPage<AccountTableOV>> queryAccounts(
            @Valid @RequestBody AccountQueryVO vo) {
        return RestBean.success(accountService.queryByConditions(vo));
    }

    @GetMapping("/delete")
    public RestBean<Void> deleteOneAccount(@RequestParam Integer id) {
        return responseUtils.messageHandle(id, accountService::logicDeleteOneAccountRecord);
    }

    @PostMapping("/update")
    public RestBean<Void> updateOneAccount(@Valid @RequestBody AccountUpdateVO vo) {
        return responseUtils.messageHandle(vo, accountService::updateOneAccount);
    }

}
