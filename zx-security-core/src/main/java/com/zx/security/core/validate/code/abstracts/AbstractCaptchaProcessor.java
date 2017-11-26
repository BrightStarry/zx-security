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
        //放入session,根据类型选择不同的key
        sessionStrategy.setAttribute(request,SESSION_CAPTCHA_PRE + type,captcha);
    }

    //最终处理
    protected abstract void process1(ServletWebRequest request, C captcha) throws Exception;

}
