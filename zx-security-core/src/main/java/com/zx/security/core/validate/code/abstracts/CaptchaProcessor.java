package com.zx.security.core.validate.code.abstracts;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * author:ZhengXing
 * datetime:2017-11-26 15:09
 * 验证码处理器
 */
public interface CaptchaProcessor {

    //session前缀
    String SESSION_CAPTCHA_PRE = "CAPTCHA_";

    //处理不同类型的验证码
    void process(String type, ServletWebRequest request) throws Exception;
}
