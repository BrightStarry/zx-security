package com.zx.security.core.authentication.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zx.security.core.properties.LoginType;
import com.zx.security.core.properties.SecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017-11-24 21:03
 * 自定义身份验证失败处理器
 */
@Component("customAuthenticationFailHandler")
@Slf4j
public class CustomAuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler{

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    /**
     * 在异常中,有错误消息,是关于为什么登录失败的
     *
     * 默认登录失败是跳转到一个登录失败的url,此处改了处理方式
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.info("登录失败");

        //如果配置的的登录方式是json,使用自定义处理器
        if(LoginType.JSON.equals(securityProperties.getBrowser().getLoginType())){
            //状态码500
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            //将authentication对象转为jsonString,返回
//            response.getWriter().write(objectMapper.writeValueAsString(e));
            response.getWriter().write(e.getMessage());
        }else{
            //否则使用父类处理方法,重定向
           super.onAuthenticationFailure(request,response,e);
        }



    }
}
