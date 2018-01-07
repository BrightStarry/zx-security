package com.zx.security.core.social.weixin.connect;

import com.zx.security.core.social.qq.api.QQ;
import com.zx.security.core.social.qq.connect.QQAdapter;
import com.zx.security.core.social.qq.connect.QQServiceProvider;
import com.zx.security.core.social.weixin.api.WeiXin;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2ServiceProvider;

/**
 * author:ZhengXing
 * datetime:2018-01-03 20:47
 */
public class WeiXinConnectionFactory extends OAuth2ConnectionFactory<WeiXin> {
    /**
     * 需要实现父类的构造方法
     *
     * @param providerId      服务提供商的唯一标识.例如'facebook'
     * serviceProvider 服务提供商的抽象,用于获取用户信息,以及url跳转等
     *  apiAdapter      适配器,
     */
    public WeiXinConnectionFactory(String providerId, String appId, String appSecret) {
        super(providerId, new WeiXinServiceProvider(appId,appSecret), null);
    }

    /**
     * 返回 换取token时,返回的openId
     */
    @Override
    protected String extractProviderUserId(AccessGrant accessGrant) {
        if(accessGrant instanceof WeiXinAccessGrant)
            return ((WeiXinAccessGrant)accessGrant).getOpenId();
        return null;
    }

    
    /**
     *  一下覆盖其创建connection的方法,只是为了 将accessGrant中包含的openId,通过extractProviderUserId方法取出,
     *  然后放入WeiXinAdapter(),再返回.因为getApiAdapter在父类中是私有的,所以才需要重写.
     */

    @Override
    public Connection<WeiXin> createConnection(AccessGrant accessGrant) {
        return new OAuth2Connection<>(getProviderId(), extractProviderUserId(accessGrant), accessGrant.getAccessToken(),
                accessGrant.getRefreshToken(), accessGrant.getExpireTime(), getOAuth2ServiceProvider(), getApiAdapter(extractProviderUserId(accessGrant)));
        
    }
    @Override
    public Connection<WeiXin> createConnection(ConnectionData data) {
        return new OAuth2Connection<>(data, getOAuth2ServiceProvider(), getApiAdapter(data.getProviderUserId()));
    }
    private ApiAdapter<WeiXin> getApiAdapter(String providerUserId) {
        return new WeiXinAdapter(providerUserId);
    }
    private OAuth2ServiceProvider<WeiXin> getOAuth2ServiceProvider() {
        return (OAuth2ServiceProvider<WeiXin>) getServiceProvider();
    }



}
