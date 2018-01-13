package com.zx.secutiry.browser;

import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.support.SimpleResponse;
import com.zx.security.core.support.SocialUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017-11-23 22:25
 */
@RestController
@Slf4j
public class BrowserSecurityController {
    /**
     * 当访问的页面需要验证时,security会跳转到下面这个接口的登录页面,
     * 但会把真正要访问的页面,也就是跳转前的页面存到cache中
     */
    private RequestCache requestCache = new HttpSessionRequestCache();

    /**
     * 重定向策略,用于跳转请求
     */
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * session失效时跳转的路径
     */
    @GetMapping("/session/invalid")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)//返回401,未授权状态码
    public SimpleResponse sessionInvalid() {
        String message = "session失效";
        return new SimpleResponse(message);
    }

    /**
     * 当需要身份认证时,跳转到这里
     *
     * 就是说.一个用户.访问了禁止未登录访问的路径.他就会跳转到此处.
     *
     * !!!!!!!!!!!!!!!!!!!!其需求是.如果!!之前!!访问的url不是页面,就返回异常信息;如果是页面,就跳转到登录页;
     * 此处是用访问的后缀是不是.html结尾来判断的,
     * @param request
     * @return
     */
    @RequestMapping("/view/login")
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)//返回401,未授权状态码
    public SimpleResponse requiredAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //获取到跳转前的请求
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        //如果请求不为空
        if (savedRequest != null) {
            //获取到请求url
            String target = savedRequest.getRedirectUrl();
            log.info("引发跳转的请求是:{}", target);
            //如果该请求是.html结尾的,跳转到登录页,否则表示不是请求的页面,返回json
            if (StringUtils.endsWithIgnoreCase(target, ".html")) {
                //跳转到登录页,从yml配置中读取登录页路径
                redirectStrategy.sendRedirect(request,response,securityProperties.getBrowser().getLoginPage());
            }
        }
        return new SimpleResponse("访问的服务需要身份认证,请引导用户到登录页");
    }


    //用来获取第三方用户信息的工具类
    @Autowired
    private ProviderSignInUtils providerSignInUtils;
    /**
     * 获取到当前已经登录的第三方用户信息
     */
    @GetMapping("/social/user")
    public SocialUserInfo getSocialUserInfo(HttpServletRequest request) {
        SocialUserInfo userInfo = new SocialUserInfo();
        //从session中获取第三方用户信息
        Connection<?> connection = providerSignInUtils.getConnectionFromSession(new ServletWebRequest(request));

        return userInfo.setProviderId(connection.getKey().getProviderId())
                .setProviderUserId(connection.getKey().getProviderUserId())
                .setNickname(connection.getDisplayName())
                .setHeadimg(connection.getImageUrl());
    }
}
