package com.zx.security.social;

import com.zx.security.core.social.SocialAuthenticationFilterPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.social.security.SocialAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2018-01-10 22:09
 * 定义app模块 社交登录,qq等服务提供商返回令牌后的不一样的操作...
 * 直接返回令牌给app
 */
@Component
public class AppSocialAuthenticationFilterPostProcessor implements SocialAuthenticationFilterPostProcessor {
    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Override
    public void process(SocialAuthenticationFilter socialAuthenticationFilter) {
        socialAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
    }
}
