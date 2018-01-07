package com.zx.security.core.social.qq.config;

import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.social.qq.connect.QQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2018-01-03 21:24
 * qq配置自动注入
 */
@Configuration
@ConditionalOnProperty(prefix = "zx.security.social.qq",name = "appId")
public class QQAutoConfig extends SocialAutoConfigurerAdapter{
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 将自定义的qq相关配置注入qq的连接工厂
     * @return
     */
    @Override
    protected ConnectionFactory<?> createConnectionFactory() {
        SecurityProperties.SocialProperties.QQProperties qq = securityProperties.getSocial().getQq();
        return new QQConnectionFactory(qq.getProviderId(),qq.getAppId(),qq.getAppSecret());
    }
}
