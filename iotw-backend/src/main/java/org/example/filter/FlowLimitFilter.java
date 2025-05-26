package org.example.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.RestBean;
import org.example.utils.Const;
import org.example.utils.FlowUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 限流控制过滤器
 * 防止用户高频请求接口，借助Redis进行限流
 *
 * @author hwshou
 * @date 2025/5/25  19:05
 */
@Component
@Order(Const.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    //指定时间内最大请求次数限制
    @Value("${spring.web.flow.limit}")
    int limit;
    //计数时间周期
    @Value("${spring.web.flow.period}")
    int period;
    //超出请求限制封禁时间
    @Value("${spring.web.flow.block}")
    int block;
    @Resource
    FlowUtils flowUtils;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String addr = request.getRemoteAddr();
        if ("OPTIONS".equals(request.getMethod()) && !this.tryCount(addr)) {
            this.writeBlockMessage(response);
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * 为响应编写拦截内容，提示用户操作频繁
     *
     * @param resp 响应
     * @throws IOException 可能的异常
     */
    private void writeBlockMessage(HttpServletResponse resp) throws IOException {
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().write(RestBean.forbidden("操作频繁请稍后再试").asJsonString());
    }

    /**
     * 尝试对指定IP地址请求计数，如果被限制则无法继续访问
     *
     * @param ip 请求IP地址
     * @return 是否操作成功
     */
    private boolean tryCount(String ip) {
        synchronized (ip.intern()) {
            if (stringRedisTemplate.hasKey(Const.FLOW_LIMIT_BLOCK + ip)) {
                return false;
            }
            String counterKey = Const.FLOW_LIMIT_COUNTER + ip;
            String blockKey = Const.FLOW_LIMIT_BLOCK + ip;
            return flowUtils.limitPeriodCheck(counterKey, blockKey, block, limit, period);
        }

    }
}
