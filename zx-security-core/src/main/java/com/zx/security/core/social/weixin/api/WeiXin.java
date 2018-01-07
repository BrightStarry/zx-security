package com.zx.security.core.social.weixin.api;

/**
 * author:ZhengXing
 * datetime:2018-01-06 14:32
 * 微信登录 接口
 */
public interface WeiXin {
    /**
     * 因为微信没有用code换取openId的步骤,
     * 而是用code换取token时,返回openId.
     * 但social未处理这一步,所以此处,获取用户信息时需要自行传入openId
     * @param openId 用户唯一标识
     * @return
     */
    WeiXinUserInfo getUserInfo(String openId);
}
