package com.zx.security.core.social.qq.connect;

import com.zx.security.core.social.qq.api.QQ;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

/**
 * author:ZhengXing
 * datetime:2018-01-03 20:47
 */
public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {
    /**
     * 需要实现父类的构造方法
     *
     * @param providerId      服务提供商的唯一标识.例如'facebook'
     * serviceProvider 服务提供商的抽象,用于获取用户信息,以及url跳转等
     *  apiAdapter      适配器
     */
    public QQConnectionFactory(String providerId,String appId,String appSecret) {
        super(providerId, new QQServiceProvider(appId,appSecret), new QQAdapter());
    }
}
