package com.zx.security.core.authentication.social;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import java.util.Collection;

/**
 * author:ZhengXing
 * datetime:2018-01-10 20:35
 * 使用openId进行 app 社交登录用户的验证
 */
@Getter
public class OpenIdAuthenticationToken extends AbstractAuthenticationToken{
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    // ~ Instance fields
    // ================================================================================================

    //认证成功前,保存openId,成功后,保存登录信息
    private final Object principal;
    //存放 服务提供商id
    private String providerId;

    // ~ Constructors
    // ===================================================================================================

    /**
     * 未登录成功时构造该类
     *
     */
    public OpenIdAuthenticationToken(String openId, String providerId) {
        super(null);
        this.principal = openId;
        this.providerId = providerId;
        //设置为还未通过校验
        setAuthenticated(false);
    }

    /**
     * 登录成功后构造该类
     * @param principal 用户信息类
     * @param authorities 用户权限集合
     */
    public OpenIdAuthenticationToken(Object principal,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true); //重写时,必须使用super的方法,通过验证
    }

    // ~ Methods
    // ========================================================================================================

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
