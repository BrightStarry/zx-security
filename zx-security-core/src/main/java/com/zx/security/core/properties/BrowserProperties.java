package com.zx.security.core.properties;

import lombok.Data;

/**
 * author:ZhengXing
 * datetime:2017-11-23 22:49
 * browser项目配置属性
 */
@Data
public class BrowserProperties {
    //登录页配置-默认值
    private String loginPage = "/login.html";

    //登录方式,登录成后后,重定向或是返回json
    private LoginType loginType = LoginType.JSON;

    //token过期时间,秒
    private Integer rememberMeSeconds = 3600;
}
