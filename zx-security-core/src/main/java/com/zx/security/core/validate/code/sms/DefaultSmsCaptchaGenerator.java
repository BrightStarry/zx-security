package com.zx.security.core.validate.code.sms;

import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.validate.code.abstracts.CaptchaGenerator;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * author:ZhengXing
 * datetime:2017-11-26 14:38
 * 短信验证码生成器
 */
@Component("smsCaptchaGenerator")
public class DefaultSmsCaptchaGenerator implements CaptchaGenerator<Captcha> {

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    public Captcha createCaptcha(ServletWebRequest request) {
        String captcha = RandomStringUtils.randomNumeric(securityProperties.getCaptcha().getSms().getLength());
        return new Captcha(captcha,securityProperties.getCaptcha().getSms().getExpireSecond());
    }
}
