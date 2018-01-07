package com.zx.security.core.social.weixin.connect;

import com.zx.security.core.social.qq.api.QQ;
import com.zx.security.core.social.qq.api.QQUserInfo;
import com.zx.security.core.social.weixin.api.WeiXin;
import com.zx.security.core.social.weixin.api.WeiXinUserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

/**
 * author:ZhengXing
 * datetime:2018-01-03 20:31
 * 适配器. 适配微信和spring social标准的信息
 */
@Getter
@Setter
@NoArgsConstructor
public class WeiXinAdapter implements ApiAdapter<WeiXin>{

    private String openId;

    public WeiXinAdapter(String openId) {
        this.openId = openId;
    }

    /**
     * 测试当前这个api(QQ) 是否可用
     * 直接返回true即可..
     * @param api
     * @return
     */
    @Override
    public boolean test(WeiXin api) {
        return true;
    }

    /**
     * 适配方法.给ConnectionValues设上值
     * 数据从QQ接口的getUserInfo方法中获取
     * @param api
     * @param values
     */
    @Override
    public void setConnectionValues(WeiXin api, ConnectionValues values) {
        WeiXinUserInfo userInfo = api.getUserInfo(openId);
        //显示的用户名
        values.setDisplayName(userInfo.getNickname());
        //用户头像
        values.setImageUrl(userInfo.getHeadimgurl());
        //个人主页
        values.setProfileUrl(null);
        //用户在服务商的id
        values.setProviderUserId(userInfo.getOpenid());
    }

    /**
     * 获取用户资料
     * @param api
     * @return
     */
    @Override
    public UserProfile fetchUserProfile(WeiXin api) {
        return null;
    }

    /**
     * 更新状态.
     * 如果API没有状态这个概念就什么都不做
     * @param api
     * @param message
     */
    @Override
    public void updateStatus(WeiXin api, String message) {
        //什么都不做
    }
}
