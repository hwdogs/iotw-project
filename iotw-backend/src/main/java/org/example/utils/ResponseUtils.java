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

    /**
     * 处理带参数的任意类型返回值业务方法
     * <p>
     * 适用场景：业务方法返回非String类型（需自定义错误检查逻辑）
     *
     * @param info         接口参数/传入信息/业务方法参数
     * @param function     业务方法引用（接受参数T，返回结果R）
     * @param errorChecker 结果检查函数（接受结果R，返回错误信息-null表示成功）
     * @param <T>          业务方法返回的数据类型（如 IPage<SupplyTableVO>）
     * @param <R>          业务方法的参数类型（如 SupplyQueryVO）
     * @return 统一响应体（成功时包含业务数据，失败时返回错误信息）
     */
    public <T, R> RestBean<R> dataHandle(T info,
                                         Function<T, R> function,
                                         Function<R, String> errorChecker) {
        R result = function.apply(info);
        String error = errorChecker.apply(result);
        return error == null ?
                RestBean.success(result) :
                RestBean.failure(400, error);
    }

    /**
     * 处理无参数的任意类型返回值业务方法
     * <p>
     * 适用场景：业务方法返回非String类型（需自定义错误检查逻辑）
     *
     * @param action       业务方法引用（无参数，返回结果R）
     * @param errorChecker 结果检查函数（接受结果R，返回错误信息-null表示成功）
     * @param <R>          业务方法返回值类型
     * @return 统一响应体（成功时包含业务数据，失败时返回错误信息）
     */
    public <R> RestBean<R> dataHandle(Supplier<R> action,
                                      Function<R, String> errorChecker) {
        R result = action.get();                   // 执行业务方法
        String error = errorChecker.apply(result); // 检查业务结果
        return error == null ?
                RestBean.success(result) :         // 成功返回业务数据
                RestBean.failure(400, error);      // 失败返回错误信息
    }
}
