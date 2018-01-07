package com.zx.security.core.social;

import org.springframework.http.MediaType;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2018-01-07 12:36
 * 自定义 用户社交帐号绑定成功  或解绑成功 回调视图
 * spring social提供了 用户登录后绑定某个社交帐号的url(/connect/{providerId})
 * 成功后,默认返回到/connect/{providerId}Connected  和/connect/{providerId}Connect
 */
public class CustomConnectedView extends AbstractView{

    /**
     * 此处将返回 社交帐号成功绑定的 页面
     * @param map 也就是model数据
     * @throws Exception
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse response) throws Exception {
        response.setContentType(MediaType.TEXT_HTML_VALUE + ";charset=UTF-8");

        //绑定成功后,有connection对象,解绑成功时没有
        if (map.get("connection") == null) {
            response.getWriter().write("<h3>解绑成功</h3>");
        }else{
            response.getWriter().write("<h3>绑定成功</h3>");

        }
    }
}
