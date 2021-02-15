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
     * 是否必须
     * <p>
     * 默认为必须，非必须的依赖在组件不存在是注入为{@code null}
     * </p>
     *
     * @return TRUE | FALSE
     */
    boolean isRequired() default true;

}
