package com.zx.security.core.social.qq;

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
     * 返回配置类
     */
    @Bean
    public SpringSocialConfigurer zxSocialSecurityConfig() {
        return new SpringSocialConfigurer();
    }

}
