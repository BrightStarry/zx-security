package com.zx.security.app;

import com.zx.security.core.authentication.mobile.SmsCaptchaAuthenticationSecurityConfig;
import com.zx.security.core.authentication.mobile.SmsCaptchaFilter;
import com.zx.security.core.authentication.social.OpenIdAuthenticationSecurityConfig;
import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.validate.code.CaptchaFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.social.security.SpringSocialConfigurer;

/**
 * author:ZhengXing
 * datetime:2018-01-07 21:54
 * 自定义资源服务配置
 */
@Configuration
@EnableResourceServer
public class CustomResourceServerConfig extends ResourceServerConfigurerAdapter{

    //短信验证码的配置类
    @Autowired
    private SmsCaptchaAuthenticationSecurityConfig smsCaptchaAuthenticationSecurityConfig;

    //springSocial配置类
    @Autowired
    private SpringSocialConfigurer zxSocialSecurityConfig;

    //图形验证码过滤器
    @Autowired
    private CaptchaFilter captchaFilter;

    //短信验证码过滤器
    @Autowired
    private SmsCaptchaFilter smsCaptchaFilter;

    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailHandler;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private OpenIdAuthenticationSecurityConfig openIdAuthenticationSecurityConfig;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http

                .apply(zxSocialSecurityConfig).and()
                //在UsernamePasswordAuthenticationFilter过滤器之前,增加上验证码过滤器
//                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(smsCaptchaFilter, UsernamePasswordAuthenticationFilter.class)
//                .httpBasic()//最原始的弹出框登录,二选一
                .formLogin()//表单页面登录,二选一
                .loginPage("/view/login")//登录页面url
                .loginProcessingUrl("/login")//登录方法url,默认就是/login,用post方法
                .successHandler(customAuthenticationSuccessHandler)//配置验证成功处理器
                .failureHandler(customAuthenticationFailHandler)//配置验证失败处理器
                .and()
                .authorizeRequests()//进行验证配置
                .antMatchers("/view/login",
                        securityProperties.getBrowser().getLoginPage(),
                        "/captcha/**",
                        securityProperties.getBrowser().getSignUpUrl(),
                        "/user/register",
                        "/session/invalid",
                        "/logout**")//匹配这些路径
                .permitAll()//全部允许
                .anyRequest()//任何请求
                .authenticated()//都需验证
                .and()
                //追加配置手机验证码过滤器
                .apply(smsCaptchaAuthenticationSecurityConfig)
                .and()
                //追加app 社交登录过滤器链
                .apply(openIdAuthenticationSecurityConfig);

        http.csrf().disable();//暂时关闭csrf,防止跨域请求的防护关闭
    }
}
