package com.zx.security.security;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * author:ZhengXing
 * datetime:2017/11/17 0017 16:49
 * 自定义user对象，继承security的user对象
 */
@Data
public class CustomUser  extends User {
    //重写父类构造函数
    public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
    //重写父类构造函数
    public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    /**
     * 自定义构造函数，增加phone属性
     * @param username
     * @param password
     * @param phone
     * @param authorities
     */
    public CustomUser(String username, String password, String phone, Collection<? extends GrantedAuthority> authorities){
        super(username, password, authorities);
        this.phone = phone;
    }


    /**
     * 增加手机字段
     */
    private String phone;
}

