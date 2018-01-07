package com.zx.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUser;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017-11-21 20:21
 * 加载用户详情接口
 */
@Component
@Slf4j
public class CustomUserDetailsService implements UserDetailsService,SocialUserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;


    /**
     * 表单登陆时,根据用户名获取用户信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("表单登录用户名:{}", username);
        //根据用户名查询用户信息

        //将用户转换为security框架中的用户
        //用户名,密码,权限
        return buildUser(username);
    }




    /**
     * 第三方登陆时,根据用户名(自己业务系统的)获取用户信息
     */
    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        log.info("第三方登录用户id:{}", userId);
        return buildUser(userId);
    }

    /**
     * 通用方法,构建用户
     * @param username
     */
    private SocialUserDetails buildUser(String username) {
        return new SocialUser(username,
                //该encode()方法实际上应该在存入密码时调用,此处应该调用match()方法
                //当为了简化,没有存入密码步骤,所以这么写,为了输入密码原文能直接通过验证
                passwordEncoder.encode("123456"),
                //几个校验方法的值
                true,true,true,true,
                //将用逗号分割的字符串转为权限集合
                AuthorityUtils.commaSeparatedStringToAuthorityList("admin,ROLE_USER"));
    }
}
