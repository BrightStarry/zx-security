package com.zx.security.core.social.weixin.config;

import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.social.CustomConnectedView;
import com.zx.security.core.social.qq.connect.QQConnectionFactory;
import com.zx.security.core.social.weixin.connect.WeiXinConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.social.SocialAutoConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.web.servlet.View;

/**
 * author:ZhengXing
 * datetime:2018-01-03 21:24
 * 微信配置自动注入
 */
@Configuration
@ConditionalOnProperty(prefix = "zx.security.social.weixin",name = "app-id")
public class WeiXinAutoConfig extends SocialAutoConfigurerAdapter{
    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 将自定义的qq相关配置注入qq的连接工厂
     * @return
     */
    @Override
    protected ConnectionFactory<?> createConnectionFactory() {
        SecurityProperties.SocialProperties.WeiXinProperties weixin = securityProperties.getSocial().getWeixin();
        return new WeiXinConnectionFactory(weixin.getProviderId(),weixin.getAppId(),weixin.getAppSecret());
    }

    /**
     * 业务用户登陆后, 绑定社交帐号 成功后的回调视图
     * 此处可以用户自定义,当没有(name = "weixinConnectedView")bean时,才使用该bean
     */
    @Bean({"connect/weixinConnected","connect/weixinConnect"})
    @ConditionalOnMissingBean(name = "weixinConnectedView")
    public View weixinConnectedView() {
        return new CustomConnectedView();
    }
}
