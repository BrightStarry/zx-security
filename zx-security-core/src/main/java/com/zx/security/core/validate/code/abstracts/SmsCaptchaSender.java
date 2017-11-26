package com.zx.security.core.validate.code.abstracts;

/**
 * author:ZhengXing
 * datetime:2017-11-26 14:19
 * 短信验证码发送接口
 */
public interface SmsCaptchaSender {
    void send(String phone, String code);
}
