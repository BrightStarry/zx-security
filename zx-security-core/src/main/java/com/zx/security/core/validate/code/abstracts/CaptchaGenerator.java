package com.zx.security.core.validate.code.abstracts;

import com.zx.security.core.validate.code.sms.Captcha;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * author:ZhengXing
 * datetime:2017-11-26 13:00
 * 验证码生成器接口
 */
public interface CaptchaGenerator<T extends Captcha> {

    T createCaptcha(ServletWebRequest request);
}
