package com.zx.async.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * author:ZhengXing
 * datetime:2017/11/20 0020 11:26
 * web配置
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

    @Autowired
    private DeferredResultProcessingInterceptor customResultInterceptor;

    //配置异步支持
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {

        configurer
                //超时时间，可在构建时传入参数覆盖该配置
                .setDefaultTimeout(3000L)
                //异步任务执行器，简单看了下AsyncTaskExecutor接口
                //应该是自行实现一个threadPoolFactory，然后实现两个执行的方法返回future就好了
                .setTaskExecutor(null)
                //注册DeferredResult对象拦截器
                .registerDeferredResultInterceptors(customResultInterceptor);
                //注册Callable的拦截器,因为目前只讨论使用DeferredResult对象,暂不赘述.
//                .registerCallableInterceptors()

    }
}
