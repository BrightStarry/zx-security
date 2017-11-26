package com.zx.security.core.authentication.mobile;

import lombok.Data;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * author:ZhengXing
 * datetime:2017-11-26 16:44
 * 短信验证码身份验证提供者
 */
@Setter
public class SmsCaptchaAuthenticationProvider implements AuthenticationProvider{

    private UserDetailsService userDetailsService;

    /**
     * 身份验证逻辑
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //因为该提供者在supports()方法中只支持自定的手机号验证,所以此处强转
        SmsCaptchaAuthenticationToken authenticationToken = (SmsCaptchaAuthenticationToken)authentication;
        //然后取出手机号,从数据库中查询对应用户
        UserDetails user = userDetailsService.loadUserByUsername((String) authenticationToken.getPrincipal());

        //如果用户为空
        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }

        //如果读取到了用户信息,重新构造Authentication并返回
        //1: 用户信息; 2: 用户权限
        SmsCaptchaAuthenticationToken result = new SmsCaptchaAuthenticationToken(user, user.getAuthorities());

        //并且,在之前的SmsCaptchaAuthenticationFilter中,
        // 将最初的request中的一些详细信息放到了最原始的authentication中(也就是该方法参数)
        //此处需要复制回来
        result.setDetails(authentication.getDetails());

        return result;
    }


    /**
     * 支持的authentication类有哪些
     * SmsCaptchaAuthenticationToken就是其实现类
     */
    @Override
    public boolean supports(Class<?> authentication) {
        //如果自己写的这个类,就是authentication的超类或本身时,支持
        return SmsCaptchaAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
