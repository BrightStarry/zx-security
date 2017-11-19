package com.zx.web.config;

import com.zx.web.filter.TimeFilter;
import com.zx.web.interceptor.TimeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017-11-19 14:57
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter{

    @Autowired
    private TimeInterceptor timeInterceptor;

    //配置异步支持
    //异步请求需要一个和同步请求不同的拦截器DeferredResultProcessingInterceptor
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        //设置异步拦截器
//        configurer.registerDeferredResultInterceptors();
        //设置异步的超时时间
        configurer.setDefaultTimeout(5000L);
        //异步执行时,线程池的配置,spring默认的线程不会重用
//        configurer.setTaskExecutor();
    }

    //配置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timeInterceptor);
    }

    //配置过滤器
    @Bean
    public FilterRegistrationBean timeFilter() {
        //创建过滤器注册器
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        //创建过滤器实例
        TimeFilter timeFilter = new TimeFilter();
        //注册
        filterRegistrationBean.setFilter(timeFilter);
        //声明过滤器要过滤的url
        List<String> urlList = new ArrayList<>();
        urlList.add("/*");
        filterRegistrationBean.setUrlPatterns(urlList);

        return filterRegistrationBean;
    }
}
