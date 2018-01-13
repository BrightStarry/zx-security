package com.zx.security.app.jwt;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2018-01-13 12:03
 * 自定义令牌增强器
 * 用于在jwt中插入自定义信息
 */
public class CustomJwtTokenEnhancer implements TokenEnhancer {
    /**
     * 增加方法
     * @param accessToken 令牌
     * @param authentication 用户信息
     * @return
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        //要放入的信息
        Map<String, Object> info = new HashMap<>();
        info.put("age", 22);
        //给令牌增加信息
        ((DefaultOAuth2AccessToken)accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}
