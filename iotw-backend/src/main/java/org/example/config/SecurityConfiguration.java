package org.example.config;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.RestBean;
import org.example.entity.dto.Account;
import org.example.entity.vo.response.AuthorizeVO;
import org.example.filter.JwtAuthorizeFiller;
import org.example.service.AccountService;
import org.example.utils.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author hwshou
 * @date 2025/5/19 14:00
 */
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    @Resource
    JwtUtils utils;

    @Resource
    JwtAuthorizeFiller authorizeFiller;

    @Resource
    AccountService service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .successHandler(this::onAuthenticationSuccess)
                        .failureHandler(this::onAuthenticationFailure))
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess))
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized) // 没有登录（没认证通过）
                        .accessDeniedHandler(this::onAccessDeny) // 认证通过但没有权限
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(authorizeFiller, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 用户认证成功执行
     *
     * @param request        HttpServletRequest请求
     * @param response       HttpServletResponse回复
     * @param authentication Authentication 认证信息
     * @throws IOException io异常
     */
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = (User) authentication.getPrincipal();
        Account account = service.findAccountByNameOrEmail(user.getUsername());
        String token = utils.createJwt(user, account.getId(), account.getUsername());

        AuthorizeVO vo = new AuthorizeVO();
        BeanUtils.copyProperties(account, vo);
        vo.setExpires(utils.expireTime());
        vo.setToken(token);

        response.getWriter().write(RestBean.success(vo).asJsonString());
    }

    /**
     * 退出登录成功执行
     *
     * @param request        HttpServletRequest请求
     * @param response       HttpServletResponse回复
     * @param authentication Authentication 认证信息
     * @throws IOException io异常
     */
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if(utils.invalidJwt(authorization)) {
            writer.write(RestBean.success().asJsonString());
        }else {
            writer.write(RestBean.failure(400,"退出登录失败").asJsonString());
        }
    }

    /**
     * 用户身份验证失败执行
     *
     * @param request   HttpServletRequest请求
     * @param response  HttpServletResponse回复
     * @param exception AuthenticationException 身份验证异常
     * @throws IOException io异常
     */
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(RestBean.unauthorize(exception.getMessage()).asJsonString());
    }

    /**
     * 认证通过但是没有权限访问特权页面
     *
     * @param request               HttpServletRequest请求
     * @param response              HttpServletResponse回复
     * @param accessDeniedException accessDeniedException 访问被拒绝异常
     * @throws IOException io异常
     */
    public void onAccessDeny(HttpServletRequest request,
                             HttpServletResponse response,
                             AccessDeniedException accessDeniedException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(RestBean.forbidden(accessDeniedException.getMessage()).asJsonString());
    }

    /**
     * 没有通过认证
     *
     * @param request       HttpServletRequest请求
     * @param response      HttpServletResponse回复
     * @param authException AuthenticationException 身份认证异常
     * @throws IOException io异常
     */
    public void onUnauthorized(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException authException) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(RestBean.unauthorize(authException.getMessage()).asJsonString());
    }
}