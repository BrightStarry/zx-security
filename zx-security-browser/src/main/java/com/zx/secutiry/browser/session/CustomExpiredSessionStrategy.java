package com.zx.secutiry.browser.session;

import org.springframework.http.MediaType;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2018-01-07 14:25
 * 自定义 session被挤下线后的策略
 */
@Component
public class CustomExpiredSessionStrategy implements SessionInformationExpiredStrategy{

    /**
     * 当被挤下线时的策略
     */
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        event.getResponse().setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        event.getResponse().getWriter().write("并发登陆,被挤下线");
    }
}
