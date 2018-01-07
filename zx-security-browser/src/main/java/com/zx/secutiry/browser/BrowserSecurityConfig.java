package com.zx.secutiry.browser;

import com.zx.security.core.authentication.mobile.SmsCaptchaAuthenticationSecurityConfig;
import com.zx.security.core.authentication.mobile.SmsCaptchaFilter;
import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.validate.code.CaptchaFilter;
import com.zx.secutiry.browser.session.CustomExpiredSessionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.social.security.SpringSocialConfigurer;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;

/**
 * author:ZhengXing
 * datetime:2017-11-19 20:29
 * 自定义浏览器安全配置
 */
@Configuration
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter{
    //图形验证码过滤器
    @Autowired
    private CaptchaFilter captchaFilter;

    //短信验证码过滤器
    @Autowired
    private SmsCaptchaFilter smsCaptchaFilter;

    //验证成功处理器
    @Autowired
    private AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    //验证失败处理器
    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailHandler;

    //数据源
    @Autowired
    private DataSource dataSource;

    //记住我功能的配置,需要注入
    @Autowired
    private UserDetailsService customUserDetailsService;

    //短信验证码的配置类
    @Autowired
    private SmsCaptchaAuthenticationSecurityConfig smsCaptchaAuthenticationSecurityConfig;

    //springSocial配置类
    @Autowired
    private SpringSocialConfigurer zxSocialSecurityConfig;

    //并发登录,session被挤下线的策略
    @Autowired
    private CustomExpiredSessionStrategy customExpiredSessionStrategy;

    /**
     * 记住我功能
     * 生成用来将token写入数据库的PersistentTokenRepository类
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        //设置在启动时,创建对应的数据库中存储token的表,只需要第一次启动时使用,或者是进去复制创表语句
//        tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }

    /**
     * 注册密码加密类,
     * 使用已有的BCryptPasswordEncoder类作为实现
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }




    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.apply(zxSocialSecurityConfig).and()
                //在UsernamePasswordAuthenticationFilter过滤器之前,增加上验证码过滤器
                .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(smsCaptchaFilter, UsernamePasswordAuthenticationFilter.class)
//                .httpBasic()//最原始的弹出框登录,二选一
                .formLogin()//表单页面登录,二选一
                    .loginPage("/view/login")//登录页面url
                    .loginProcessingUrl("/login")//登录方法url,默认就是/login,用post方法
                    .successHandler(customAuthenticationSuccessHandler)//配置验证成功处理器
                    .failureHandler(customAuthenticationFailHandler)//配置验证失败处理器
                .and()
                .rememberMe()//配置记住我功能
                    //token仓库配置,用来将token存入数据库
                    .tokenRepository(persistentTokenRepository())
                    //token过期秒数配置
                    .tokenValiditySeconds(securityProperties.getBrowser().getRememberMeSeconds())
                    //查询用户信息的service
                    .userDetailsService(customUserDetailsService)
                .and()
                .sessionManagement()
                    .invalidSessionUrl("/session/invalid")//session失效后跳转到的路径
//                    .invalidSessionStrategy(InvalidSessionStrategy)//可以自定义session失效时的策略
                    .maximumSessions(1)//同一session同一时间最大数量,一般就是1,也就是不同机器登录会被挤下线
                    .maxSessionsPreventsLogin(true)//该参数表示,当session并发到达最大值后,不允许后来者再登录
//                    .expiredUrl("xxx")//session被挤下线后跳转的url
                    .expiredSessionStrategy(customExpiredSessionStrategy)//被挤下线后的自定义策略
                .and()//这个and返回SessionManagementConfigurer
                .and()//这个and才返回原配置类
                .logout()
                    .logoutUrl("/logout")//请求注销的url,默认是/logout
                    .logoutSuccessUrl("/logout.html")//注销成功后跳转到的路径
//                    .logoutSuccessHandler()//自定义注销成功后的处理逻辑
//                    .deleteCookies("")//注销时可删除指定key的cookies
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
                //追加配置
                .apply(smsCaptchaAuthenticationSecurityConfig);

        http.csrf().disable();//暂时关闭csrf,防止跨域请求的防护关闭
    }
}
