package com.zx.security.core.social;

import org.springframework.social.security.SocialAuthenticationFilter;

/**
 * author:ZhengXing
 * datetime:2018-01-10 22:00
 * 用来在 社交登录 成功获取到令牌后,根据是app或浏览器执行不同处理
 * app需要返回令牌,浏览器需要跳转页面
 */
public interface SocialAuthenticationFilterPostProcessor {

    void process(SocialAuthenticationFilter socialAuthenticationFilter);
}
