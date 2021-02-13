package com.github.ompc.athing.standard.thing.boot;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 设备组件依赖注解
 */
@Target(FIELD)
@Retention(RUNTIME)
public @interface ThDepend {

    /**
     * 组件ID
     *
     * @return 组件ID
     */
    String value() default "";

}
