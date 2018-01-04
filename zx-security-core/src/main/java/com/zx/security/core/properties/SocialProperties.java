package com.zx.security.core.properties;

import lombok.Data;

/**
 * author:ZhengXing
 * datetime:2018-01-03 21:20
 * social相关配置
 */
@Data
public class SocialProperties {

    //social通用的处理url,默认的social框架默认的相同.都是/auth,配置在此处不过是可以自行修改而已
    private String filterProcessesUrl = "/auth";

    private QQProperties qq = new QQProperties();
}
