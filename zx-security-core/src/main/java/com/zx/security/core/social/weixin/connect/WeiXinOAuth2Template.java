package com.zx.security.core.social.weixin.connect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zx.security.core.social.weixin.api.WeiXinImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2018-01-04 21:17
 * 微信  自定义的Oauth2协议http请求模版
 * 需要扩展一个返回对象 openId
 */
@Slf4j
public class WeiXinOAuth2Template extends OAuth2Template {
    //微信刷新token 延长有效时间的url
    private static final String REFRESH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/refresh_token";

    //因为注入到父类属性获取不到
    private String accessTokenUrl;
    private String clientId;
    private String clientSecret;

    /**
     * 微信中, 因为已经重写了 exchangeForAccess方法.该参数的赋值可以不要
     */
    public WeiXinOAuth2Template(String clientId, String clientSecret, String authorizeUrl, String accessTokenUrl) {
        super(clientId, clientSecret, authorizeUrl, accessTokenUrl);
//
//        //父类中使用该参数来判断, 换取accessToken时是否携带某些参数.qq是需要携带的
        setUseParametersForClientAuthentication(true);
        //父类是私有属性,获取不到.
        this.accessTokenUrl = accessTokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @SneakyThrows
    @Override
    public AccessGrant refreshAccess(String refreshToken, MultiValueMap<String, String> additionalParameters) {
        StringBuilder url = new StringBuilder(REFRESH_TOKEN_URL)
                .append("?appid=").append(clientId)
                .append("&grant_type=refresh_token")
                .append("&refresh_token=").append(refreshToken);

        return getAccessGrant(url);
    }

    /**
     * 重写 将用户引导到 微信登录页面的url. 因为微信的请求参数不同.
     */
    @Override
    public String buildAuthenticateUrl(OAuth2Parameters parameters) {
        String url = super.buildAuthenticateUrl(parameters);
        url = url + "&appid="+clientId+"&scope=snsapi_login";
        return url;
    }

    /**
     * 该方法在父类中和buildAuthenticateUrl方法的区别应该是参数的不同而已.
     * 主要是在 登录业务用户后,再调用connect/{providerId}接口绑定社交帐号时,
     * 创建出来的一个类的useAuthenticateUrl变量默认是false,会调用该方法,而不是上面的方法.
     * 所以需要重写该方法.
     */
    @Override
    public String buildAuthorizeUrl(OAuth2Parameters parameters) {
        return buildAuthenticateUrl(parameters);
    }

    /**
     * 重写默认创建模版的方法,
     * 这里和下面方法的不同之处在于,下面的方法是为了修改编码,此处是为了将消息转换器集合 注入 restTemplate
     * See{@link WeiXinImpl#getMessageConverters()}
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
     * 重写换取 token的方法. 因为微信没有遵守OAuth2协议....传的参数名不一样
     */
    @SneakyThrows
    @Override
    public AccessGrant exchangeForAccess(String authorizationCode, String redirectUri, MultiValueMap<String, String> additionalParameters) {
        StringBuilder url = new StringBuilder(accessTokenUrl)
                .append("?appid=").append(clientId)
                .append("&secret=").append(clientSecret)
                .append("&code=").append(authorizationCode)
                .append("&grant_type=authorization_code")
                .append("&redirect_uri=").append(redirectUri);

        return getAccessGrant(url);
    }


    /**
     * 复用. 第一次获取token 和 刷新token
     */
    private AccessGrant getAccessGrant(StringBuilder url) throws IOException {
        String result = getRestTemplate().getForObject(url.toString(), String.class);

        Map<String,Object> resultMap = new ObjectMapper().readValue(result, Map.class);

        //返回错误码时,抛出异常
        if(StringUtils.isNotBlank(MapUtils.getString(resultMap,"errcode"))){
            throw new RuntimeException("获取access token失败,errorcode:" +
                    MapUtils.getString(resultMap, "errcode") + ", errmsg:" +
                    MapUtils.getString(resultMap, "errMsg"));
        }

        //返回自定义的 accessGrant ,扩展了个openId
        return new WeiXinAccessGrant(
                MapUtils.getString(resultMap, "openid"),
                MapUtils.getString(resultMap, "access_token"),
                MapUtils.getString(resultMap, "scope"),
                MapUtils.getString(resultMap, "refresh_token"),
                MapUtils.getLong(resultMap, "expires_in")
        );
    }


}
