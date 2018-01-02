package com.zx.security.core.social.qq.api;

import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2018-01-02 21:20
 * 获取qq用户信息的接口
 */
public interface QQ {

    QQUserInfo getUserInfo() throws Exception;
}
