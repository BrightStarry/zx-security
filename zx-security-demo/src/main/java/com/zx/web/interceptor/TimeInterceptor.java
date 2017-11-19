package com.zx.web.interceptor;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017-11-19 15:17
 * 时间处理拦截器
 */
@Component
public class TimeInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        System.out.println("preHandle");
        httpServletRequest.setAttribute("startTime",new Date().getTime());

        System.out.println(((HandlerMethod)o).getBean().getClass().getName());
        System.out.println(((HandlerMethod)o).getMethod().getName());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        Long startTime = (Long) httpServletRequest.getAttribute("startTime");
        System.out.println("postHandle-拦截器耗时:" + (new Date().getTime() - startTime));
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        Long startTime = (Long) httpServletRequest.getAttribute("startTime");
        System.out.println("afterCompletion-拦截器耗时:" + (new Date().getTime() - startTime));
        System.out.println("exception is" + e);
    }
}
