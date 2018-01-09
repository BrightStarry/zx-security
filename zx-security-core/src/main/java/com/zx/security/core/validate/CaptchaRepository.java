package com.zx.security.core.validate;

import com.zx.security.core.validate.code.sms.Captcha;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.Serializable;

/**
 * author:ZhengXing
 * datetime:2018-01-08 22:36
 * session仓库 接口
 * 因为app没有session机制,所以验证码等缓存信息需要存入redis,和普通web的机制不同
 */
public interface CaptchaRepository {

    //保存
    void put(ServletWebRequest request, String key, Captcha captcha);

    //获取
    Captcha get(ServletWebRequest request,String key);

    //删除
    void remove(ServletWebRequest request,String key);
}
