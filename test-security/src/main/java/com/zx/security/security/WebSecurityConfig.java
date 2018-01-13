package com.zx.security.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

/**
 * author:ZhengXing
 * datetime:2017/11/17 0017 10:05
 * spring security 配置类
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserService customUserService;

    @Autowired
    private CustomSecurityInterceptorFilter securityInterceptorFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
//                    .antMatchers("/js/**","/").permitAll()//允许访问此处所有路径
                    .anyRequest().authenticated()//验证访问
                    .and()
                .formLogin()
                    .loginPage("/login")//登录页面
                    .successForwardUrl("/")
                    .failureUrl("/login/error")//失败页面
                    .permitAll()
                    .and()
                .logout()//登出页面
                    .permitAll();

        http.csrf().disable();//security框架为防止跨域请求，需要post请求带上csrf参数，暂停该策略

        //增加前一个过滤器
        http.addFilterBefore(securityInterceptorFilter, FilterSecurityInterceptor.class);

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //使用UserDetailsService来查询用户名、密码等用户信息
        auth.userDetailsService(customUserService);
    }
}
