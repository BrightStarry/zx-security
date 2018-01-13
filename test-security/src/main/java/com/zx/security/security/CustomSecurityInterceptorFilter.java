package com.zx.security.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.access.intercept.AbstractSecurityInterceptor;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;


/**
 * author:ZhengXing
 * datetime:2017-11-18 14:54
 * 自定义安全拦截器过滤器
 */
@Component
public class CustomSecurityInterceptorFilter extends AbstractSecurityInterceptor implements Filter {

    //用来加载所有权限资源，并将请求url和权限url匹配
    @Autowired
    private FilterInvocationSecurityMetadataSource securityMetadataSource;


    //将自定义权限决定管理器set到父类中，用来验证某用户是否拥有某权限
    @Autowired
    public void setCustomAccessDecisionManager(CustomAccessDecisionManager customAccessDecisionManager) {
        super.setAccessDecisionManager(customAccessDecisionManager);
    }

    //过滤器方法
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //过滤器调用,这个对象里面存储了拦截下来的请求
        FilterInvocation filterInvocation = new FilterInvocation(servletRequest, servletResponse, filterChain);

        /**
         * 在该调用器内部，将用注入的securityMetadataSource获取请求的url对应的权限，
         * 然后使用注入的CustomAccessDecisionManager,将权限与用户拥有的权限比对，
         */
        InterceptorStatusToken interceptorStatusToken = super.beforeInvocation(filterInvocation);

        //执行下一个拦截器
        try {
            filterInvocation.getChain().doFilter(servletRequest,servletResponse);
        } finally {
            super.afterInvocation(interceptorStatusToken, null);
        }


    }



    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    //返回SecurityMetadataSource
    @Override
    public SecurityMetadataSource obtainSecurityMetadataSource() {
        return this.securityMetadataSource;
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }



    @Override
    public void destroy() {

    }
}
