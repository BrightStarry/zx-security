package com.zx.security.core.social.weixin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-01-06 14:36
 * 微信 接口实现类
 */
@Getter
@Setter
@Slf4j
public class WeiXinImpl extends AbstractOAuth2ApiBinding implements WeiXin{
    /**
     * 获取用户信息接口
     */
    private static final String URL_GER_USERINFO = "https://api.weixin.qq.com/sns/userinfo?openid=%s";
    


    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 此处和qq不同的是,直接注入了openID,而不是自己去获取,因为在上一步code换取token时已经返回了openId
     */
    public WeiXinImpl(String accessToken) {
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
    }


    /**
     *  重写获取消息转换器的方法,因为默认的String消息转换器的编码是ISO-8859-1
     */
    @Override
    protected List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> messageConverters = super.getMessageConverters();
        messageConverters.remove(0);
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return messageConverters;
    }

    /**
     * 获取用户信息
     */
    @Override
    public WeiXinUserInfo getUserInfo(String openId) {
        //发起请求,获取用户信息
        String url = String.format(URL_GER_USERINFO, openId);
        String result = getRestTemplate().getForObject(url, String.class);
        log.info("未解析返回用户信息:{}", result);
        //json转换,并附上openId
        try {
            WeiXinUserInfo userInfo = objectMapper.readValue(result, WeiXinUserInfo.class);
            log.info("获取用户信息:{}",userInfo);
            return userInfo;
        } catch (IOException e) {
            throw new RuntimeException("获取用户信息失败", e);
        }
    }
}
