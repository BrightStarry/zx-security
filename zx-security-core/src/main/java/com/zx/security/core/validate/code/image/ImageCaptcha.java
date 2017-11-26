package com.zx.security.core.validate.code.image;

import com.zx.security.core.validate.code.sms.Captcha;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.awt.image.BufferedImage;

/**
 * author:ZhengXing
 * datetime:2017-11-26 14:05
 * 图形验证码
 * 因为和短信验证相比,只是多一个BufferedImage属性,所以可以直接继承,并添加该属性;;;
 * 原逻辑是我有一个Captcha是图形验证码,然后短信验证码只比他少一个image属性,.....
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ImageCaptcha extends Captcha {

    //图片流
    private BufferedImage image;


    public ImageCaptcha(BufferedImage image, String code, int expireSecond) {
        super(code,expireSecond);
        this.image = image;
    }



}
