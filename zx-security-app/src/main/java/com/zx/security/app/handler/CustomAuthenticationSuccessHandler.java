package com.zx.security.app.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zx.security.core.properties.LoginType;
import com.zx.security.core.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017-11-24 20:40
 * 自定义身份验证成功处理器
 * app模块中.该类的作用是.
 * 我们的其他应用(例如app),会请求我们的access_token.当验证通过后.
 * 通过该类,将对应的令牌,返回给他们
 *
 * 同时.当app的社交登录时,app会引导用户社交登录.同时qq等服务提供商会将授权码返回给app.
 * 此时就需要app携带该授权码请求我们.成功后.我们会将qq分配的access_token返回回去.
 * 然后app再根据其openId,向我们请求我们的access_token.
 */
@Component("customAuthenticationSuccessHandler")
@Slf4j
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    /**
     * springMVC在启动时自动注册的bean,用于将对象转为json
     */
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;



    //用来根据client_id查询应用信息,springSecurity默认已经配置好了,直接注入即可
    @Autowired
    private ClientDetailsService clientDetailsService;
    //用来构造令牌的类
    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;
    /**
     * 当登陆成功时
     *
     * @param request
     * @param response
     * @param authentication 封装了认证信息
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("登录成功");
        /**
         * 解析请求头中的 Authorization
         *
         * header传参的格式(也就是basic auth方式)
         "Authorization" : "bearer eb715f85-e813-4586-bef0-5a4545248ca0"
         可以从已有的BasicAuthenticationFilter中获取从请求头中获取client_id的代码
         */
        String header = request.getHeader("Authorization");
        //如果没有,或者,不以Basic 开头.
        if (header == null || !header.startsWith("Basic ")) {
            throw new UnapprovedClientAuthenticationException("请求头中无client信息");
        }

        String[] tokens = extractAndDecodeHeader(header, request);
        assert tokens.length == 2;

        /**
         * 获取ClientDetails并验证
         */
        String clientId = tokens[0];
        String clientSecret = tokens[1];
        //查询应用信息
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        //此处无需做非空判断,因为service中已经做过
        //只需判断密码是否一致
        if (!StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
            throw new UnapprovedClientAuthenticationException("clientSecret不匹配:" + clientId);
        }

        /**
         * 构造
         * 1:请求的参数Map,用来构造Authentication,我们有了,直接传空
         * 4.OAuth2的模式,此处我们是自定义协议,就随便传个custom
         */
        TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), "custom");

        //创建OAuth2Request
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

        //创建oAuth2Authentication
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);

        /**
         * 创建token
         */
        OAuth2AccessToken accessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);

        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        //返回令牌
        response.getWriter().write(objectMapper.writeValueAsString(accessToken));
    }

    /**
     * 对 请求头中的client_id 和密码进行Base64解码
     */
    private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
            throws IOException {

        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }
        //解码后的client_id和密码格式为  <client_id>:<密码>
        String token = new String(decoded, "UTF-8");

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        //冒号前为client_id,冒号后为密码
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }

}
