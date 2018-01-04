package com.zx.security.core.social.qq;

import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.social.CustomSpringSocialConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

/**
 * author:ZhengXing
 * datetime:2018-01-03 20:53
 * 配置类
 */
@Configuration
@EnableSocial
public class SocialConfig extends SocialConfigurerAdapter{

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 实现获取UsersConnectionRepository的方法
     *
     * ConnectionFactoryLocator用来获取连接工厂(可能有多个.所以要查找)
     */
    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        //第三个参数是对存入的数据加密,此处不做任何加密
        return new JdbcUsersConnectionRepository(dataSource, connectionFactoryLocator, Encryptors.noOpText());
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
        String filterProcessesUrl = securityProperties.getSocial().getFilterProcessesUrl();
        return new CustomSpringSocialConfigurer(filterProcessesUrl);
    }

}
