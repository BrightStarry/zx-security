package com.zx.security.core.social.weixin.connect;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.social.oauth2.AccessGrant;

/**
 * author:ZhengXing
 * datetime:2018-01-06 15:19
 * 这个对象是 用 code换取token的返回值.
 * 微信需要扩展下,增加openId.
 */
@Getter
@Setter
@ToString
public class WeiXinAccessGrant extends AccessGrant {

    /**openId*/
    private String openId;

    public WeiXinAccessGrant(String openId, String accessToken, String scope, String refreshToken, Long expiresIn) {
        super(accessToken, scope, refreshToken, expiresIn);
        this.openId = openId;
    }
}
