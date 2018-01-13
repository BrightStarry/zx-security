package com.zx.security;

import org.springframework.context.annotation.Bean;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2018-01-06 13:02
 * 实现该接口,可以让social自动调用该方法,
 * 将第一次第三方登录直接注册到业务系统中,不再跳转到绑定或注册页面
 */
public class CustomConnectionSignUp implements ConnectionSignUp{
    /**
     * 根据第三方用户信息. 在业务系统用户表中创建用户,并返回 用户唯一标识,以绑定到social的关联表中
     */
    @Override
    public String execute(Connection<?> connection) {
        //此处暂时用 昵称表示用户唯一标识
        return connection.getDisplayName();
    }
}
