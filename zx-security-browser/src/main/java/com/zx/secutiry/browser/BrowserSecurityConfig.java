package com.zx.secutiry.browser;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * author:ZhengXing
 * datetime:2017-11-19 20:29
 * 自定义浏览器安全配置
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter{


    /**
     * 注册密码加密类,
     * 使用已有的BCryptPasswordEncoder类作为实现
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .httpBasic()//最原始的弹出框登录,二选一
                .formLogin()//表单页面登录,二选一
                .and()
                .authorizeRequests()//进行验证配置
                .anyRequest()//任何请求
                .authenticated();//都需验证

    }
}
