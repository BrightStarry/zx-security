package com.zx.security.core.validate.code;

import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.validate.code.abstracts.CaptchaGenerator;
import com.zx.security.core.validate.code.image.DefaultImageCaptchaGenerator;
import com.zx.security.core.validate.code.image.ImageCaptcha;
import com.zx.security.core.validate.code.sms.DefaultSmsCaptchaSender;
import com.zx.security.core.validate.code.abstracts.SmsCaptchaSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author:ZhengXing
 * datetime:2017-11-26 13:05
 * 验证码bean配置类
 */
//该注解表示将在该类中使用@Bean配置bean,相当于用java代码写原先的spring.xml中的beans标签配置bean
@Configuration
public class CaptchaBeanConfig {
    @Autowired
    private SecurityProperties securityProperties;
    //当spring在容器中无法找到名字为imageCaptchaGenerator的bean的时候,才使用该方法生成bean
    @Bean
    @ConditionalOnMissingBean(name = "imageCaptchaGenerator")
    public CaptchaGenerator<ImageCaptcha> imageCaptchaGenerator() {
        //创建默认的图片验证码生成器
        DefaultImageCaptchaGenerator imageCaptchaGenerator = new DefaultImageCaptchaGenerator();
        imageCaptchaGenerator.setSecurityProperties(securityProperties);
        return imageCaptchaGenerator;
    }

    @Bean
    @ConditionalOnMissingBean(SmsCaptchaSender.class)
    public SmsCaptchaSender smsCaptchaSender() {
        //创建短信验证码发送器
        return new DefaultSmsCaptchaSender();
    }
}
