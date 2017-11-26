package com.zx.security.core.authentication.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zx.security.core.properties.LoginType;
import com.zx.security.core.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
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
 * security默认在验证成功后跳转到此前访问的页面,但是如果前端的登录是
 * ajax方式的,不适合跳转页面,所以需要更改成功后的处理
 */
@Component("customAuthenticationSuccessHandler")
@Slf4j
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{
    /**
     * springMVC在启动时自动注册的bean,用于将对象转为json
     */
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;


    /**
     * 当登陆成功时
     * @param request
     * @param response
     * @param authentication 封装了认证信息
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("登录成功");

        //如果配置的的登录方式是json,使用自定义处理器
        if(LoginType.JSON.equals(securityProperties.getBrowser().getLoginType())){
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            //将authentication对象转为jsonString,返回
            response.getWriter().write(objectMapper.writeValueAsString(authentication));
        }else{
            //否则使用父类处理方法,重定向
            super.onAuthenticationSuccess(request,response,authentication);
        }




    }
}
