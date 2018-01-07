package com.zx.security.core.validate.code.sms;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * author:ZhengXing
 * datetime:2017-11-26 10:34
 * 验证码
 */
@Data
@AllArgsConstructor
public class Captcha implements Serializable{

    //随机数
    protected String code;

    //过期时间
    protected LocalDateTime expireTime;



    public Captcha(String code, int expireSecond) {
        this.code = code;
        //当前时间 + 过期秒数  = 过期时间
        this.expireTime = LocalDateTime.now().plusSeconds(expireSecond);
    }


    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
