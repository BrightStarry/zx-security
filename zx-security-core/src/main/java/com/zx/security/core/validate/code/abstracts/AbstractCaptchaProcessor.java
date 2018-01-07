package com.zx.security.core.validate.code.abstracts;

import com.zx.security.core.validate.code.sms.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017-11-26 15:11
 * 抽象 验证码处理器类 模版方法
 */
public abstract class AbstractCaptchaProcessor<C extends Captcha> implements CaptchaProcessor {

    //session策略
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    /**
     * 保存所有CaptchaGenerator,用每个bean的name为key,不过我觉得可以直接写个CaptchaGenerator,
     * 然后在子类中任意注入
     */
    @Autowired
    private Map<String,CaptchaGenerator> captchaGeneratorMap;

    @Override
    public void process(String type, ServletWebRequest request) throws Exception {
        //创建验证码
        C captcha= create(type,request);
        //保存验证码
        save(type,request,captcha);
        //最终处理验证码
        process1(request,captcha);
    }




    //创建
    private C create(String type,ServletWebRequest request) {
        return (C)captchaGeneratorMap.get(type + "CaptchaGenerator").createCaptcha(request);
    }

    //保存
    private void save(String type,ServletWebRequest request, Captcha captcha) {
        /**
         * 此处的转换是因为
         * 该验证码需要存入session,而session需要存入redis.
         * 而存入redis的所有对象包括对象中的属性对象都需要实现序列化接口.
         * 而如果是图形验证码,则会有一个 图片流 对象没有实现序列化,抛弃他生成新的只有code和过期时间的对象即可
         */

        Captcha captcha1 = new Captcha(captcha.getCode(), captcha.getExpireTime());

        //放入session,根据类型选择不同的key
        sessionStrategy.setAttribute(request,SESSION_CAPTCHA_PRE + type,captcha1);
    }

    //最终处理
    protected abstract void process1(ServletWebRequest request, C captcha) throws Exception;

}
