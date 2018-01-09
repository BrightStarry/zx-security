package com.zx.security.core.authentication.mobile;

import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.core.validate.CaptchaRepository;
import com.zx.security.core.validate.code.CaptchaException;
import com.zx.security.core.validate.code.abstracts.CaptchaProcessor;
import com.zx.security.core.validate.code.image.ImageCaptcha;
import com.zx.security.core.validate.code.sms.Captcha;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.executable.ValidateOnExecution;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * author:ZhengXing
 * datetime:2017-11-26 11:18
 * 短信验证码过滤器,继承OncePerRequestFilter类后,保证该过滤器只会被调用一次
 */
@Component
@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class SmsCaptchaFilter extends OncePerRequestFilter implements InitializingBean{

    @Autowired
    private AuthenticationFailureHandler customAuthenticationFailHandler;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    private Set<String> urlSet = new HashSet<>();

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private CaptchaRepository captchaRepository;

    //springSecurity中用来判断某个路径是否包含某个路径的工具类,例如/user/** 包含了/user/a
    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    //在初始化bean完成后,调用下面的方法
    @Override
    public void afterPropertiesSet() throws ServletException {
        super.afterPropertiesSet();//父类的实现不能删除
        //分割字符串过程中会按照每个分隔符进行分割，不忽略任何空白项；
        String[] urls = StringUtils.splitByWholeSeparatorPreserveAllTokens(securityProperties.getCaptcha().getSms().getUrl(), ",");
        //将urls加入set
        CollectionUtils.addAll(urlSet,urls);

        //并且,一定加入登录请求
        urlSet.add("/login/phone");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //遍历循环,判断访问的url是否包含在需要验证验证码的url中
        boolean flag = false;
        for (String s : urlSet) {
            if(antPathMatcher.match(s,request.getRequestURI())){
                flag = true;
                break;
            }

        }

        if (flag) {

            /**
             * 教程中在此处使用了security的登录失败处理器,执行相关处理,
             * 但我认为没有必要,此处只需要执行项目最外层的springMVC的异常处理即可,
             * 根据请求是否需要返回html/text返回错误页面或者是错误json;;
             *
             * 结果是我蠢了.还没mvc的异常捕获捕获不了
             */
            try {
                validate(new ServletWebRequest(request));
            } catch (CaptchaException e) {
                customAuthenticationFailHandler.onAuthenticationFailure(request,response,e);
                return;
            }
        }
        //如果不是登录请求,直接调用后面的过滤器
        filterChain.doFilter(request,response);
    }

    //验证验证码是否正确
    private void validate(ServletWebRequest request) throws ServletRequestBindingException {
        Captcha captcha = captchaRepository.get(request, CaptchaProcessor.SESSION_CAPTCHA_PRE + "sms");
        //从session中获取正确的验证码
//        Captcha captcha = (Captcha) sessionStrategy.getAttribute(request, CaptchaProcessor.SESSION_CAPTCHA_PRE + "sms");
        //获取请求中的验证码
        String requestCaptcha = ServletRequestUtils.getStringParameter(request.getRequest(), "smsCaptcha");

        //校验
        if(captcha == null)
            throw new CaptchaException("验证码不存在");
        if(StringUtils.isBlank(requestCaptcha))
            throw new CaptchaException("验证码不能为空");
//        if(captcha.isExpired()){
//            sessionStrategy.removeAttribute(request, CaptchaProcessor.SESSION_CAPTCHA_PRE + "sms");
//            throw new CaptchaException("验证码已经过期");
//        }
        if(!StringUtils.equalsIgnoreCase(captcha.getCode(),requestCaptcha))
            throw new CaptchaException("验证码不正确");



        //执行到此处,表示验证通过
//        sessionStrategy.removeAttribute(request,CaptchaProcessor.SESSION_CAPTCHA_PRE + "sms");
        captchaRepository.remove(request, CaptchaProcessor.SESSION_CAPTCHA_PRE + "sms");

    }
}
