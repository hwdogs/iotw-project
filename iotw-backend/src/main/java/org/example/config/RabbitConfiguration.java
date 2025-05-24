package org.example.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ消息队列配置
 *
 * @author hwshou
 * @date 2025/5/24 20:39
 */
@Configuration
public class RabbitConfiguration {

    /**
     * 配置消息转换器，用于序列化/反序列化 RabbitMQ 消息体
     * 使用 JSON 格式替代默认的 Java 序列化，提升安全性和跨语言兼容性
     * 安全特性：
     * - 规避 CVE-2023-34050 反序列化漏洞
     * - 通过白名单机制限制可反序列化的类
     *
     * @see Jackson2JsonMessageConverter
     */
    @Bean
    public MessageConverter messageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper());
        return converter;
    }

    /**
     * 配置类映射器，定义可信赖的反序列化类包路径
     * 安全规则：
     * - 仅允许 java.util（集合类）和 com.example（业务类）的反序列化
     * - 防止恶意攻击者注入未授权类
     *
     * @see DefaultClassMapper#setTrustedPackages(String...)
     */
    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("java.util", "org.example");  // 信任的包名
        return classMapper;
    }

    /**
     * 声明邮件队列的持久化配置
     * 队列特性：
     * - 持久化（durable=true）：Broker 重启后队列不丢失[6](@ref)
     * - 非排他（exclusive=false）：允许多消费者连接
     * - 非自动删除（autoDelete=false）：连接断开时保留队列
     *
     * @see QueueBuilder#durable(String)
     */
    @Bean("mailQueue")
    public Queue queue() {
        return QueueBuilder
                .durable("mail")
                .build();
    }
}
