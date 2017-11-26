package com.zx.security.core.authentication.mobile;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * author:ZhengXing
 * datetime:2017-11-26 16:23
 * 短信验证码身份验证token
 * 将{@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}
 * 整个拷贝过来
 *
 * 该类在认证成功前,放手机号,成功后,放登录信息
 */
public class SmsCaptchaAuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // ~ Instance fields
    // ================================================================================================

    //在认证成功前,放手机号,成功后,放登录信息
    private final Object principal;
    //在原类中放密码
//    private Object credentials;

    // ~ Constructors
    // ===================================================================================================

    /**
     * 未登录时构造该类
     *
     */
    public SmsCaptchaAuthenticationToken(String phone) {
        super(null);
        this.principal = phone;
        setAuthenticated(false);//未通过校验
    }

    /**
     * 登录成功后构造该类
     */
    public SmsCaptchaAuthenticationToken(Object principal,
                                               Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true); //重写时,必须使用super的方法,通过验证
    }

    // ~ Methods
    // ========================================================================================================

    //给方法是父类抽象方法.不能删除
    public Object getCredentials() {
        return null;
    }

    public Object getPrincipal() {
        return this.principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
    }
}
