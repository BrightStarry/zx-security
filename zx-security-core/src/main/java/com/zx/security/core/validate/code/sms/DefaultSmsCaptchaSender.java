package com.zx.security.core.validate.code.sms;

import com.zx.security.core.validate.code.abstracts.SmsCaptchaSender;
import lombok.extern.slf4j.Slf4j;

/**
 * author:ZhengXing
 * datetime:2017-11-26 14:21
 * 默认的短信验证码发送接口
 */
@Slf4j
public class DefaultSmsCaptchaSender implements SmsCaptchaSender {
    @Override
    public void send(String phone, String code) {
        log.info("向手机:{},发送短信验证码:{}",phone,code);
    }
}
