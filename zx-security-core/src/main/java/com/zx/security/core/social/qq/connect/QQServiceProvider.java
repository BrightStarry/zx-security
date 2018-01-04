package com.zx.security.core.social.qq.connect;

import com.zx.security.core.social.qq.api.QQ;
import com.zx.security.core.social.qq.api.QQImpl;
import org.springframework.social.oauth2.AbstractOAuth2ServiceProvider;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Template;

/**
 * author:ZhengXing
 * datetime:2018-01-02 22:01
 * QQ服务提供商
 *
 * 其父类泛型是 获取用户信息的接口
 */
public class QQServiceProvider extends AbstractOAuth2ServiceProvider<QQ>{

    //QQ分配的应用id
    private String appId;

    //将用户导向认证服务器登录的url
    private static final String URL_AUTHORIZE = "https://graph.qq.com/oauth2.0/authorize";

    //用用户认证成功返回的授权码换取token的url
    private static final String URL_ACCESS_TOKEN = "https://graph.qq.com/oauth2.0/token";

    /**
     * 父类只有这么一个构造函数,所以我们必须传入OAuth2Operations,
     * 也就是    操作接口,封装了OAuth2协议,从用户授权到获取令牌的所有流程
     *
     */
    public QQServiceProvider(String appId,String appSecret) {
        /**
         * 暂时使用其自带的类
         * 其参数
         * 1. appId  qq分配的,相当于用户名
         * 2. appSecret qq分配的,相当于密码
         * 3. 对应授权码模式第1步,将用户导向认证服务器时的url(让用户去认证服务器进行登录)
         * 4. 对应授权码模式第4步,用户在认证服务器认证成功,并返回授权码,我们用授权码去换取令牌的url
         */
        super(new QQOAuth2Template(appId,appSecret,URL_AUTHORIZE,URL_ACCESS_TOKEN));
        this.appId = appId;
    }

    /**
     * 创建出一个QQImpl用来获取用户信息,
     * @param accessToken
     * @return
     */
    @Override
    public QQ getApi(String accessToken) {
        return new QQImpl(accessToken,appId);
    }
}
