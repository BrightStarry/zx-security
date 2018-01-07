package com.zx.security.core.social.qq.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.social.oauth2.AbstractOAuth2ApiBinding;
import org.springframework.social.oauth2.TokenStrategy;

import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2018-01-02 21:22
 * QQ接口实现类, 获取用户信息
 */
@Data
@Slf4j
public class QQImpl extends AbstractOAuth2ApiBinding implements QQ {

    /**
     * 通过token换取用户openId的接口.末尾是%s,方便等下直接替换
     */
    private static final String URL_GET_OPENID = "https://graph.qq.com/oauth2.0/me?access_token=%s";

    /**
     * 通过appId/openId和父类的accessToken获取用户信息接口.
     * 其中的access_token参数交由父类处理.剩余两个参数也用%s替换,方便等下替换上参数
     */
    private static final String URL_GET_USERINFO =
            "https://graph.qq.com/user/get_user_info?" +
                    "&oauth_consumer_key=%s&openid=%s";


    /**
     * QQ分配给每个应用的标识
     */
    private String appId;

    /**
     * 当前登录的qq用户的id.类似于qq号
     */
    private String openId;

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据accessToken 和 appid构造
     * @param accessToken
     * @param appId
     */
    public QQImpl(String accessToken, String appId) {
        //第二个参数是token策略.如果不传,默认是将access_token放入http header.
        //但是qq需要将其作为参数,所以选择该策略
        super(accessToken, TokenStrategy.ACCESS_TOKEN_PARAMETER);
        this.appId = appId;

        /**
         * 发送请求获取openId
         */
        //用token替换掉%s
        String url = String.format(URL_GET_OPENID, accessToken);
        //发送请求,获取返回值
        String result = getRestTemplate().getForObject(url, String.class);

        //callback( {"client_id":"100550231","openid":"0E68EE8447B17286294053A99490A1A5"} )
        log.info("openId返回值:{}",result);

        //从返回值中获取openid

        this.openId = StringUtils.substringBetween(result,"\"openid\":\"","\"}");
    }

    @Override
    public QQUserInfo getUserInfo(){
        //发起请求,获取用户信息,还一个参数accessToken交由父类的token策略自行处理
        String url = String.format(URL_GET_USERINFO, appId, openId);
        String result = getRestTemplate().getForObject(url, String.class);
        //json转换,并附上openId
        try {
            QQUserInfo qqUserInfo = objectMapper.readValue(result, QQUserInfo.class).setOpenId(openId);
            log.info("获取用户信息:{}",qqUserInfo);
            return qqUserInfo;
        } catch (IOException e) {
            throw new RuntimeException("获取用户信息失败", e);
        }
    }
}
