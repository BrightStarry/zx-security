package com.zx.security.app;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * author:ZhengXing
 * datetime:2018-01-07 21:03
 * 自定义认证服务配置
 */
@Configuration
@EnableAuthorizationServer //开启认证服务器
public class CustomAuthorizationServerConfig {
}
