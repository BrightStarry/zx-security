package com.zx.security.core.social;

import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.social.CustomSpringSocialConfigurer;
import com.zx.security.core.social.SocialAuthenticationFilterPostProcessor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * author:ZhengXing
 * datetime:2018-01-03 20:53
 * 配置类
 */
@Order(1)
@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter{

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired(required = false)
    private ConnectionSignUp connectionSignUp;

    //自定义的用来处理返回令牌操作的处理器
    @Autowired(required = false)
    private SocialAuthenticationFilterPostProcessor socialAuthenticationFilterPostProcessor;

    /**
     * 实现获取UsersConnectionRepository的方法
     *
     * ConnectionFactoryLocator用来获取连接工厂(可能有多个.所以要查找)
     */
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        //第三个参数是对存入的数据加密,此处不做任何加密
        JdbcUsersConnectionRepository jdbcUsersConnectionRepository = new JdbcUsersConnectionRepository(
                dataSource, connectionFactoryLocator, Encryptors.noOpText());
        //直接将 用来实现第一次第三方登录的用户直接在 业务系统中创建对应用户的 逻辑的接口注入.他可能为空.
        jdbcUsersConnectionRepository.setConnectionSignUp(connectionSignUp);
        return jdbcUsersConnectionRepository;
    }

    /**
     * 将SpringSocial配置类加入spring Bean,以用来注入SpringSecurity配置类
     * 之所以直接返回,只因为该类中已经有了默认的一些配置,
     * 例如将SocialAuthenticationFilter过滤器加入过滤器链等
     *
     * 返回自定义的配置类
     */
    @Bean
    public SpringSocialConfigurer zxSocialSecurityConfig() {
        //social要拦截的路径,默认为/auth
        String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
        CustomSpringSocialConfigurer configurer = new CustomSpringSocialConfigurer(filterProcessesUrl);
        //修改默认的注册路径
        configurer.signupUrl(securityProperties.getBrowser().getSignUpUrl());
        //注入返回令牌处理器,可能为空
        configurer.setSocialAuthenticationFilterPostProcessor(socialAuthenticationFilterPostProcessor);
        return configurer;
    }


    /**
     * 这个服务提供者 注册 工具类 的作用就是
     * 在注册中如何获取到已经通过验证的第三方用户信息,
     * 以及登录成功后.如何将注册好的业务系统用户信息传回给social.
     */
    @Bean
    public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator connectionFactoryLocator) {
        return new ProviderSignInUtils(connectionFactoryLocator,
                getUsersConnectionRepository(connectionFactoryLocator));
    }

}
