package com.acouchis.safelyrunner;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SafelyRunner {


    /**
     * 重定向错误到 SpEl
     * 需要使用root定位
     * root
     *
     * @return
     * @see SafelyRunnerAspect.Root
     * <p>
     * inner class root{
     * // 返回值
     * private Object returnObject;
     * // 入参
     * private Object[] args;
     * }
     */
    String target() default "";

    /**
     * 指定收集的错误
     * 默认 Throwable.class 即几乎不会抛出任何错误
     * @see Throwable
     */

    Class<? extends Throwable> source() default Throwable.class;

    /**
     * 如果指定了错误信息，那么就将覆盖 ex.getMessage()
     *
     * @return
     */
    String specifiedMessage() default "";

}
