package com.zx.security.core.validate.code;

import com.zx.security.core.validate.code.abstracts.CaptchaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017-11-26 10:39
 * 验证码控制层
 */
@RestController
public class CaptchaController {
//
//    //验证码存储在session中的key
//    public static final String SESSION_CAPTCHA_KEY = "CAPTCHA";
//
//    //session策略
//    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();
//
//    //图片验证码生成器
//    @Autowired
//    private CaptchaGenerator<ImageCaptcha> imageCaptchaGenerator;
//
//    //短信验证码生成器
//    @Autowired
//    private CaptchaGenerator<Captcha> smsCaptchaGenerator;
//
//    //短信验证码发送器
//    @Autowired
//    private SmsCaptchaSender smsCaptchaSender;

    @Autowired
    private Map<String,CaptchaProcessor> captchaProcessorMap;


    //图形验证码
    //短信验证码
    @GetMapping("/captcha/{type}")
    public void createCode(@PathVariable String type, HttpServletRequest request, HttpServletResponse response) throws Exception {
       captchaProcessorMap.get(type + "CaptchaProcessor").process(type,new ServletWebRequest(request,response));
    }




}
