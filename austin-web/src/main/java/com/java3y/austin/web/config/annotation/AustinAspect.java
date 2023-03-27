package com.java3y.austin.web.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志打印切面注解
 * @author kl
 * @version 1.0.0
 * @description 接口切面注解
 * @date 2023/2/23 9:01
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AustinAspect {

}
