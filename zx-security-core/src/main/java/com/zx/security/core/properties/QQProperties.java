package com.zx.security.core.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.social.SocialProperties;

/**
 * author:ZhengXing
 * datetime:2018-01-03 21:16
 * qq的一些配置属性
 */
@Data
public class QQProperties extends SocialProperties {

    /**
     * 服务提供商唯一标识
     */
    private String providerId = "qq";
}
