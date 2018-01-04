package com.zx.security.core.social.qq.connect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2018-01-04 21:17
 * 自定义的Oauth2协议http请求模版
 * 需要将原类,增加支持text/html格式的令牌结果响应
 */
@Slf4j
public class QQOAuth2Template  extends OAuth2Template{
    public QQOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);

        //父类中使用该参数来判断, 换取accessToken时是否携带某些参数.qq是需要携带的
        setUseParametersForClientAuthentication(true);
    }

    /**
     * 重写默认创建模版的方法
     * @return
     */
    @Override
    protected RestTemplate createRestTemplate() {
        //先获取到父类创建出来的该对象
        RestTemplate restTemplate = super.createRestTemplate();
        //增加一个处理string返回值的消息转换器
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        return restTemplate;
    }

    /**
     * 重写发送请求,并获取响应结果的方法
     * 父类中,是将响应结果转为map返回.然后将map转为AccessGrant对像返回
     */
    @Override
    protected AccessGrant postForAccessGrant(String accessTokenUrl, MultiValueMap<String, String> parameters) {
        //和父类一样发送请求,但响应类型是string
        String result = getRestTemplate().postForObject(accessTokenUrl, parameters, String.class);
        log.info("获取accessToken的响应:{}",result);

        //该分割方法   ""不会被忽略,可以设置分割字符串的数组长度
        String[] items = StringUtils.splitByWholeSeparatorPreserveAllTokens(result, "&");

        //截取从某个字符开始到字符末尾的 所有字符
        String accessToken = StringUtils.substringAfterLast(items[0], "=");
        Long expireIn = new Long(StringUtils.substringAfterLast(items[1], "="));
        String refreshToken = StringUtils.substringAfterLast(items[2], "=");


        return new AccessGrant(accessToken, null, refreshToken, expireIn);
    }
}
