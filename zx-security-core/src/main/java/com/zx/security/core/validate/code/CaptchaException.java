package com.zx.security.core.validate.code;

import org.springframework.security.core.AuthenticationException;

/**
 * author:ZhengXing
 * datetime:2017-11-26 11:24
 * 验证码异常类
 */
public class CaptchaException extends AuthenticationException {

    public CaptchaException(String msg) {
        super(msg);
    }
}
