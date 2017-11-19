package com.zx.web.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017-11-19 15:33
 * 自定义切片AOP
 *
 * aop时间:
 * @After 之后
 * @Before 之前
 * @AfterThrowing 抛出异常后
 * @Around 包含上面三个注解
 */
@Aspect
@Component
public class TimeAspect {
    //拦截任何返回值的userController中任何方法任何参数
    @Around("execution(* com.zx.web.controller.UserController.*(..))")
    public Object handleControllerMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        System.out.println("time aspect start");
        
        //获取方法所有参数
        Object[] params = proceedingJoinPoint.getArgs();
        for (Object param : params) {
            System.out.println("param is " + param );
        }

        long startTime = new Date().getTime();
        //执行真正的方法
        Object result = proceedingJoinPoint.proceed();
        System.out.println("time aspect 耗时:" + (new Date().getTime() - startTime));
        System.out.println("time aspect end");
        return result;
    }
}
