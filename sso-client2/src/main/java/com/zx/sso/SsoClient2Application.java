package com.zx.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * author:ZhengXing
 * datetime:2018-01-13 14:42
 */
@SpringBootApplication
@RestController
@EnableOAuth2Sso
public class SsoClient2Application {

    /**
     * 自动获取当前系统登录的用户信息(此处从请求头中的jwt中解析出Authentication)
     */
    @GetMapping("/user")
    public Authentication user(Authentication user, HttpServletRequest request) {
        return user;
    }

    public static void main(String[] args) {
        SpringApplication.run(SsoClient2Application.class, args);
    }
}
