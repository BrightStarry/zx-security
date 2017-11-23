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
}
