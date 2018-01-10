package com.zx.security.core.authentication.mobile;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author:ZhengXing
 * datetime:2017-11-26 16:31
 * 短信验证身份过滤器
 *
 * 将{@link org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter}
 * 代码整个拷过来
 */
public class SmsCaptchaAuthenticationFilter  extends AbstractAuthenticationProcessingFilter {
    // ~ 静态字段初始化
    // =====================================================================================

    //请求中,手机号参数的key
    public static final String FORM_PHONE_KEY = "phone";
    private String phoneParameter = FORM_PHONE_KEY;
    //是否只处理post请求
    private boolean postOnly = true;

    // ~ 构造方法
    // ===================================================================================================

    public SmsCaptchaAuthenticationFilter() {
        //该类用来处理哪个请求
        super(new AntPathRequestMatcher("/login/phone", "POST"));
    }

    // ~ 其他方法
    // ========================================================================================================

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        //请求方法是否为post
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        //获取手机号
        String phone = obtainPhone(request);

        if (phone == null) {
            phone = "";
        }

        phone = phone.trim();

        //实例化一个AuthenticationToken
        SmsCaptchaAuthenticationToken authRequest = new SmsCaptchaAuthenticationToken(phone);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }



    /**
     * 获取手机号的方法
     */
    private String obtainPhone(HttpServletRequest request) {
        return request.getParameter(phoneParameter);
    }

    /**
     * 将请求的详细信息,放入SmsCaptchaAuthenticationToken
     *
     */
    private void setDetails(HttpServletRequest request,
                            SmsCaptchaAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }

    /**
     * 设置手机号
     */
    public void setPhoneParameter(String phoneParameter) {
        Assert.hasText(phoneParameter, "Username parameter must not be empty or null");
        this.phoneParameter = phoneParameter;
    }

    /**
     * 设置是否只处理post请求
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }

    /**
     * 获取手机号
     */
    public final String getPhoneParameter() {
        return phoneParameter;
    }

}