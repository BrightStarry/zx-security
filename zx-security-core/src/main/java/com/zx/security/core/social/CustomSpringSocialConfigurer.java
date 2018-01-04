package com.zx.security.core.social;

import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * author:ZhengXing
 * datetime:2018-01-04 20:17
 * 自定义{@link org.springframework.social.security.SpringSocialConfigurer}
 * 用来自定义 回调地址等属性
 */
public class CustomSpringSocialConfigurer extends SpringSocialConfigurer{
    //该过滤器要处理的url
    private String filterProcessesUrl;

    //通过构造函数传入
    public CustomSpringSocialConfigurer(String filterProcessesUrl) {
        this.filterProcessesUrl = filterProcessesUrl;
    }

    /**
     * 重写该方法
     * @param object 就是要加入到SpringSecurity过滤器链上的social的过滤器
     */
    @Override
    protected <T> T postProcess(T object) {
        //用父类方法处理后,再将结果转回原类型进行扩展
        SocialAuthenticationFilter filter = (SocialAuthenticationFilter) super.postProcess(object);
        //修改要处理的url
        filter.setFilterProcessesUrl(filterProcessesUrl);
        return (T)filter;
    }
}
