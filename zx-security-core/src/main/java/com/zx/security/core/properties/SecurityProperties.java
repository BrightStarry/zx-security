package com.zx.security.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * author:ZhengXing
 * datetime:2017-11-23 22:48
 * 安全配置属性
 */
@Data
@ConfigurationProperties(prefix = "zx.security")
public class SecurityProperties {
    //浏览器端配置
    private BrowserProperties browser = new BrowserProperties();
    //验证码相关配置
    private CaptchaProperties captcha = new CaptchaProperties();
    //social相关配置
    private SocialProperties social = new SocialProperties();
}
