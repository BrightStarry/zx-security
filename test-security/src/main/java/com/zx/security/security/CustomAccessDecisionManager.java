package com.zx.security.security;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * author:ZhengXing
 * datetime:2017-11-18 14:00
 * 自定义进入权限决定管理器
 *
 *
 */
@Component
public class CustomAccessDecisionManager implements AccessDecisionManager{

    /**
     * 使用该方法判断一个请求是否允许进入，也就是是否拥有其对应请求url的权限
     *
     * @param authentication {@link CustomUserService#loadUserByUsername(String)}中，
     *                       添加到User中的List<GrantedAuthority>，包含了每个用户的所有权限
     * @param o              包含客户端发起的请求的requset信息，可转换为 HttpServletRequest request = ((FilterInvocation) object).getHttpRequest();
     * @param collection     {@link CustomInvocationSecurityMetadataSourceService#getAttributes(Object)}方法返回的对象，
     *                       也就是某个权限的一些信息;也就是{@link CustomInvocationSecurityMetadataSourceService#permissionUrlMap}的value；
     *                       按照我目前的代码，返回的也就是用户请求对应的权限名
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> collection) throws AccessDeniedException, InsufficientAuthenticationException {
        //如果该权限信息集合为空， 表示用户访问的url无需权限，放行
        if (CollectionUtils.isEmpty(collection))
            return;

        //获取权限名
        ConfigAttribute permissionConfig = ((ArrayList<ConfigAttribute>) collection).get(0);
        String permissionName = permissionConfig.getAttribute();

        //当前用户允许访问的权限集合
        Collection<? extends GrantedAuthority> allowPermissionCollection = authentication.getAuthorities();

        //遍历该集合，与当前访问的url权限一一比对，如果有，则放行
        for (GrantedAuthority grantedAuthority : allowPermissionCollection) {
            if(grantedAuthority.getAuthority().equals(permissionName))
                return;
        }

        //否则，抛出异常
        throw new AccessDeniedException("权限不足");

    }

    //此处需要为true
    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    //此处需要为true
    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
