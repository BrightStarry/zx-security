package com.zx.security.core.validate.code.sms;

import com.zx.security.core.validate.code.abstracts.AbstractCaptchaProcessor;
import com.zx.security.core.validate.code.abstracts.SmsCaptchaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

/**
 * author:ZhengXing
 * datetime:2017-11-26 15:38
 * 短信验证码处理,模版方法实现类
 */
@Component
public class SmsCaptchaProcessor extends AbstractCaptchaProcessor<Captcha> {
    @Autowired
    private SmsCaptchaSender smsCaptchaSender;

    @Override
    protected void process1(ServletWebRequest request, Captcha captcha) throws ServletRequestBindingException {
        //从request中获取到手机号,并发送验证码
        smsCaptchaSender.send(
                ServletRequestUtils.getRequiredStringParameter(
                        request.getRequest(),
                        "phone"),
                captcha.getCode());
    }
}
