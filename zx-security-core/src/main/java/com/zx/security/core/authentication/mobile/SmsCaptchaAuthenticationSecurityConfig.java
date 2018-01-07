package com.zx.security.core.authentication.mobile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017-11-26 17:07
 * 短信验证码登录配置
 * 因为该配置在app和browser中通用,所以写在core中
 */
@Component
public class SmsCaptchaAuthenticationSecurityConfig
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain,HttpSecurity> {

    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailHandler;

    @Autowired
    private UserDetailsService customUserDetailsService;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        //过滤器
        SmsCaptchaAuthenticationFilter smsCaptchaAuthenticationFilter = new SmsCaptchaAuthenticationFilter();
        //将AuthenticationManager注入给过滤器
        smsCaptchaAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        //指定其验证成功和验证失败handler
        smsCaptchaAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        smsCaptchaAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailHandler);

        //身份验证提供者
        SmsCaptchaAuthenticationProvider smsCaptchaAuthenticationProvider = new SmsCaptchaAuthenticationProvider();
        //设置userDetailsService
        smsCaptchaAuthenticationProvider.setUserDetailsService(customUserDetailsService);

        //加入配置
        http.authenticationProvider(smsCaptchaAuthenticationProvider)
                //将该过滤器加到UsernamePasswordAuthenticationFilter过滤器后边
                .addFilterAfter(smsCaptchaAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
