package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解，用于标识某个方法需要进行功能字段自动填充处理
 */
//Target和Retention是所有注解都要实现的
//指定注解加到什么位置,指定注解只能加到方法上面：
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    //指定当前数据库操作的类型（通过枚举的方式指定）：UPDATA INSERT
    OperationType value();
}
