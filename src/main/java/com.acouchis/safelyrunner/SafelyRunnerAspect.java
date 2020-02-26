package com.acouchis.safelyrunner;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by gaopeng09 on 2020-02-25
 */

@Slf4j
@Aspect
@Configuration
@SuppressWarnings("unchecked")
public class SafelyRunnerAspect {
    @Pointcut("@annotation(SafelyRunner)")
    private void safelyRunnerPointcut() {
    }


    @Around("safelyRunnerPointcut()&&@annotation(safelyRunner)")
    public <T> T around(ProceedingJoinPoint joinPoint, SafelyRunner safelyRunner) {

        T t;
        try {
            t = (T) joinPoint.proceed();
        } catch (Throwable e) {
            t = newReturnObject(((MethodSignature) joinPoint.getSignature()).getReturnType());
            try {
                // 如果是子类，那么执行，如果不是子类，会报错，跳过错误处理逻辑
                e.getClass().asSubclass(safelyRunner.source());
                ExpressionParser parser = new SpelExpressionParser();
                EvaluationContext context = new StandardEvaluationContext(new Root(t, joinPoint.getArgs()));
                Expression expression = parser.parseExpression(safelyRunner.target());
                expression.setValue(context,
                        "".equals(safelyRunner.specifiedMessage()) ? e.getMessage() : safelyRunner.specifiedMessage());
            } catch (ClassCastException ignore) {

            }

        }
        return t;
    }

    private static <T> T newReturnObject(Class<?> clazz) {
        T result = null;
        try {
            Constructor<?> defaultConstructor = clazz.getConstructor();
            result = (T) defaultConstructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            log.error("can not new an object to return at class [{}]", clazz, e);
        }
        return result;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class Root {
        // 返回值
        private Object returnObject;
        // 入参
        private Object[] args;

    }
}
