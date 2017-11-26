package com.zx.security.core.properties;

import lombok.Data;

/**
 * author:ZhengXing
 * datetime:2017-11-26 12:09
 * 所有图形验证码的配置属性,
 * 包括图形验证码和短信验证码
 */
@Data
public class CaptchaProperties {
    private ImageCaptchaProperties image = new ImageCaptchaProperties();
    private SmsCaptchaProperties sms = new SmsCaptchaProperties();
}
