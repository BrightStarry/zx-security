package com.zx.security.core.properties;

import lombok.Data;

/**
 * author:ZhengXing
 * datetime:2017-11-26 14:41
 * 短信验证码配置属性
 */
@Data
public class SmsCaptchaProperties {
    //验证码长度
    protected Integer length = 4;
    //过期时间
    protected Integer expireSecond = 60;
    //需要手机验证的url
    protected String url = "";
}
