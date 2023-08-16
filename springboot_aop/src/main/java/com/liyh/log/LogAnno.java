package com.liyh.log;

import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: liyh
 * @Date: 2020/9/17 17:12
 */
@Target({ElementType.METHOD,ElementType.TYPE})  //作用于方法 使用在类，接口
@Retention(RetentionPolicy.RUNTIME)     //运行时有效
public @interface LogAnno {
    @AliasFor("value")
    String[] operating() default {};
    @AliasFor("operating")
    String[] value() default {};
}
