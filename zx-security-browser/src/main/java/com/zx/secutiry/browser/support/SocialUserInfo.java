package com.zx.secutiry.browser.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * author:ZhengXing
 * datetime:2018-01-06 12:39
 * 第三方登陆后通用的用户信息
 * 用于第三方用户登录成功后.第一次登陆时.在注册或绑定自己的业务系统帐号,前台可以展示的该第三方用户信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SocialUserInfo {

    /**服务商唯一标识,例如qq*/
    private String providerId;
    
    /**用户在服务商的唯一标识, 就是openId*/
    private String providerUserId;

    /**用户昵称*/
    private String nickname;
    
    /**头像*/
    private String headimg;
}
