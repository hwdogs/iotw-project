package org.example.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Consumer;

/**
 * 可以快速转化为指定的vo对象
 * 反射实现实体到VO的自动转换
 *
 * @author hwshou
 * @date 2025/5/20  17:39
 */
public interface BaseData {

    /**
     * 将当前实体转换为指定的视图对象（VO），并允许通过Consumer进行后处理
     *
     * @param clazz    目标VO的类类型，必须包含无参构造器
     * @param consumer 用于对生成的VO对象进行额外处理的函数（可进行字段补充/校验等操作）
     * @param <V>      视图对象（View Object）的类型
     * @return 经过处理的VO对象实例
     * @throws RuntimeException 如果反射操作失败（如无参构造器不存在/字段访问失败）
     * @示例 转换后补充计算字段：
     * {@code
     * UserVO vo = user.asViewObject(UserVO.class, v ->
     * v.setFullName(user.getFirstName() + " " + user.getLastName())
     * );
     * }
     */
    default <V> V asViewObject(Class<V> clazz, Consumer<V> consumer) {
        V v = this.asViewObject(clazz); //先调用原有方法生成VO对象
        consumer.accept(v);             //通过Consumer对VO对象进行后处理
        return v;                       //返回处理后的VO对象
    }

    /**
     * 将当前实体对象转换为目标VO对象
     *
     * @param clazz 目标VO的类类型
     * @param <V>   各种实体类
     * @return 实体类对应VO对象
     */
    default <V> V asViewObject(Class<V> clazz) {
        try {
            Field[] declaredFields = clazz.getDeclaredFields();     //通过反射获取VO的所有字段
            Constructor<V> constructor = clazz.getConstructor();    //获取VO的无参构造器
            V v = constructor.newInstance();                        //通过构造器构造vo对象实例
            for (Field declaredField : declaredFields) {            //遍历VO的每个字段，
                convert(declaredField, v);                          //调用convert方法将实体类的字段值复制到VO对象。
            }
            return v;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 将实体类的字段值复制到VO的同名字段
     *
     * @param field 反射
     * @param vo    VO对象实例
     */
    private void convert(Field field, Object vo) {
        try {
            Field source = this.getClass().getDeclaredField(field.getName());   //通过反射获取实体类中与VO字段同名的字段
            field.setAccessible(true);                                          //设置字段可访问，绕过访问修饰符限制。
            source.setAccessible(true);
            field.set(vo, source.get(this));                                    //将实体字段的值赋给VO字段
        } catch (IllegalAccessException | NoSuchFieldException ignored) {       //忽略字段不存在或访问异常的异常
        }
    }

}
