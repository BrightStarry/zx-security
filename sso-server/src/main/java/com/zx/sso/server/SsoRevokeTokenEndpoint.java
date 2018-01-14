package com.zx.sso.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * author:ZhengXing
 * datetime:2018-01-14 10:07
 */
@FrameworkEndpoint
public class SsoRevokeTokenEndpoint {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    @GetMapping("/oauth/delete/token")
    @ResponseBody
    public boolean revokeToken(String accessToken) {
        return consumerTokenServices.revokeToken(accessToken);
    }
}
