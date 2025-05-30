package org.example.utils;

import org.example.entity.RestBean;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 对返回前端的响应进行封装
 *
 * @author hwshou
 * @date 2025/5/29  13:01
 */
@Component
public class ResponseUtils {

    /**
     * 对于只有一个参数的service层接口，可以进一步简化
     *
     * @param info     接口参数/传入信息
     * @param function service层函数
     * @param <T>      函数参数
     * @return 封装的统一响应回复体
     */
    public <T> RestBean<Void> messageHandle(T info, Function<T, String> function) {
        return messageHandle(() -> function.apply(info));
    }

    /**
     * 针对于返回值为String作为错误信息的方法进行统一处理
     *
     * @param action 具体操作
     * @param <T>    响应结果类型
     * @return 响应结果
     */
    public <T> RestBean<T> messageHandle(Supplier<String> action) {
        String message = action.get();
        return message == null ? RestBean.success() : RestBean.failure(400, message);
    }
}
