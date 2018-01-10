package com.zx.security.core.authentication.social;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * author:ZhengXing
 * datetime:2018-01-10 20:46
 * app  社交登录 身份验证过滤器
 */
public class OpenIdAuthenticationFilter extends AbstractAuthenticationProcessingFilter{
    // ~ Static fields/initializers
    // =====================================================================================

    //请求中openId和providerId的key
    public static final String FORM_OPEN_ID_KEY= "openId";
    public static final String FORM_PROVIDER_ID_KEY = "providerId";

    //是否只处理post
    private boolean postOnly = true;

    // ~ Constructors
    // ===================================================================================================

    public OpenIdAuthenticationFilter() {
        //该类用来处理哪个路径
        super(new AntPathRequestMatcher("/authentication/openId", "POST"));
    }

    // ~ Methods
    // ========================================================================================================

    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        if (postOnly && !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        //获取openId,如果为空,就用""
        String openId = ServletRequestUtils.getStringParameter(request, FORM_OPEN_ID_KEY, "").trim();
        //获取providerId
        String providerId = ServletRequestUtils.getStringParameter(request, FORM_PROVIDER_ID_KEY, "").trim();

        //构建还未认证的 token存储
        OpenIdAuthenticationToken authRequest = new OpenIdAuthenticationToken(openId, providerId);

        // Allow subclasses to set the "details" property
        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    /**
     * 将请求的详细信息,放入OpenIdAuthenticationToken
     */
    protected void setDetails(HttpServletRequest request,
                              OpenIdAuthenticationToken authRequest) {
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));
    }



    /**
     * set
     */
    public void setPostOnly(boolean postOnly) {
        this.postOnly = postOnly;
    }


}
