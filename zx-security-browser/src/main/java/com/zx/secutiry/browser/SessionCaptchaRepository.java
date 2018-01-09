package com.zx.secutiry.browser;

import com.zx.security.core.validate.CaptchaRepository;
import com.zx.security.core.validate.code.sms.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * author:ZhengXing
 * datetime:2018-01-09 21:23
 *
 * 使用session存储验证码
 */
@Component
public class SessionCaptchaRepository implements CaptchaRepository {
    @Autowired
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @Override
    public void put(ServletWebRequest request, String key, Captcha captcha) {
        sessionStrategy.setAttribute(request,key,captcha);
    }

    @Override
    public Captcha get(ServletWebRequest request, String key) {
        return (Captcha)sessionStrategy.getAttribute(request, key);
    }

    @Override
    public void remove(ServletWebRequest request, String key) {
        sessionStrategy.removeAttribute(request,key);
    }
}
