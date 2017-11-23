package com.zx.secutiry.browser;

import com.zx.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .httpBasic()//最原始的弹出框登录,二选一
                .formLogin()//表单页面登录,二选一
                .loginPage("/view/login")//登录页面url
                .loginProcessingUrl("/login")//登录方法url,默认就是/login,用post方法

                .and()
                .authorizeRequests()//进行验证配置

                .antMatchers("/view/login",securityProperties.getBrowser().getLoginPage())//匹配这些路径
                .permitAll()//全部允许

                .anyRequest()//任何请求
                .authenticated();//都需验证

        http.csrf().disable();//暂时关闭csrf,防止跨域请求的防护关闭
    }
}
