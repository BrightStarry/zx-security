package com.zx.security.core.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * author:ZhengXing
 * datetime:2017-11-26 12:05
 * 图形验证码配置属性
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ImageCaptchaProperties extends SmsCaptchaProperties{
    private Integer width = 67;
    private Integer height = 23;

    //这样在继承后也可以修改length的默认值,而不会和短信验证码的发生长度冲突
    public ImageCaptchaProperties() {
        setLength(4);
    }
}
