package com.zx.security.core.authentication.social;

import lombok.Setter;
import org.apache.commons.collections.SetUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * author:ZhengXing
 * datetime:2018-01-10 21:04
 * app 社交登录 身份验证类
 */
@Setter
public class OpenIdAuthenticationProvider implements AuthenticationProvider{

    //用来查询系统用户,
    private SocialUserDetailsService userDetailsService;

    //用来查询 social框架的 社交用户和系统业务用户关联表的repository
    private UsersConnectionRepository usersConnectionRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //强转-此处不会失败,因为supports方法通过后才会调用该方法
        OpenIdAuthenticationToken authenticationToken = (OpenIdAuthenticationToken) authentication;

        //repository需要set类型的id集合
        HashSet<String> providerUserIds = new HashSet<>();
        providerUserIds.add((String) authenticationToken.getPrincipal());

        //查询出该providerId/openId的用户的业务系统id集合
        Set<String> userIds = usersConnectionRepository.findUserIdsConnectedTo(authenticationToken.getProviderId(),
                providerUserIds);

        //获取到用户id,并查询系统用户
        String userId = userIds.iterator().next();
        SocialUserDetails user = userDetailsService.loadUserByUserId(userId);
        if (user == null) {
            throw new InternalAuthenticationServiceException("无法获取用户信息");
        }
        //创建存储用户信息的AuthenticationToken
        OpenIdAuthenticationToken authenticationTokenResult = new OpenIdAuthenticationToken(user, user.getAuthorities());

        //将在Filter类中放入的Request类和请求参数获取到 成功的这个 token对象中
        authenticationTokenResult.setDetails(authenticationToken.getDetails());

        return authenticationTokenResult;
    }


    /**
     * 如果过滤器拦截下的,并获取到属性,创建了的对应AuthenticationToken对象,
     * 符合该类要验证的对象的条件,就返回true,表示支持验证该对象.
     * 应该是使用了责任链模式
     *
     * 此处是判断对象是否是自己定义的那个token类的子类
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return OpenIdAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
