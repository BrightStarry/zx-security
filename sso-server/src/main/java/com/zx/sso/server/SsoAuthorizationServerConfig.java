package com.zx.sso.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * author:ZhengXing
 * datetime:2018-01-13 14:22
 *
 * 认证服务器配置类
 */
@Configuration
@EnableAuthorizationServer
public class SsoAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    /**
     * 配置两个应用client1 和 client2
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("client1")
                .secret("123456")
                .authorizedGrantTypes("authorization_code","refresh_token")
                .scopes("all")
                .and()
                .withClient("client2")
                .secret("123456")
                .authorizedGrantTypes("authorization_code","refresh_token")
                .scopes("all");
    }

    /**
     * jwt令牌存储
     */
    @Bean
    public TokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    /**
     * jwt令牌生成
     */
    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //签名 防篡改 用的密钥
        jwtAccessTokenConverter.setSigningKey("zx");
        return jwtAccessTokenConverter;
    }

    /**
     * 认证服务器服务端配置
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(jwtTokenStore())
                .accessTokenConverter(jwtAccessTokenConverter());
    }

    /**
     * 认证服务器安全配置
     * 让应用AB可以通过身份验证获取签名密钥
     * 因为应用AB需要和该服务端一致的token签名密钥才能 验证token
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //配置访问tokenKey(也就是jwt的签名密钥)的需要的认证.
                //使用授权表达式, 此处表示需要验证身份认证才能获取到该签名密钥
                //默认是 "denyAll()",拒绝所有访问
                .tokenKeyAccess("isAuthenticated()");

    }
}
