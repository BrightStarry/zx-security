package com.zx.async.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2017/11/20 0020 14:50
 * 该拦截器是处理使用Callable的controller异步方法的.暂不赘述
 *
 * 自定义可回调的处理拦截器,实现{@link CallableProcessingInterceptor}接口,
 * 同样有一个{@link org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter}类,
 * 可以继承,不重写不需要重写的方法
 *
 * 具体的执行顺序可参照{@link CustomResultInterceptor}类
 *
 * 此外,也有一个{@link org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor}类
 * 重写了超时方法,只是抛出一个异常
 */
@Component
@Slf4j
public class CustomCallableProcessingInterceptor implements CallableProcessingInterceptor {
    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
        log.info("callable拦截器,beforeConcurrentHandling方法");
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
        log.info("callable拦截器,preProcess方法");
    }

    @Override
    public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
        log.info("callable拦截器,postProcess方法");
    }

    @Override
    public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        log.info("callable拦截器,handleTimeout方法");
        return null;
    }

    @Override
    public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
        log.info("callable拦截器,afterCompletion方法");
    }
}
