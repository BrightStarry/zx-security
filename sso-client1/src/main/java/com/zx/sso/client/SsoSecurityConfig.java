package com.zx.sso.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * author:ZhengXing
 * datetime:2018-01-13 15:22
 * security配置类
 */
@EnableOAuth2Sso
@Configuration
public class SsoSecurityConfig extends WebSecurityConfigurerAdapter {

    //通用安全配置
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .logout()
                .logoutUrl("/logout")
                .and()
                .authorizeRequests()
                .antMatchers("/", "/login**","/logout")
                .permitAll()
                .anyRequest()
                .authenticated();

    }
}
