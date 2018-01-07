package com.zx.security.core.social;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.social.connect.Connection;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2018-01-07 12:36
 * 自定义 用户社交帐号绑定状态 视图
 * spring social提供了查询业务帐号 其 绑定社交帐号 状态的一个接口,
 * 默认返回到这么一个视图
 */
@Component("connect/status")
public class CustomConnectionStatusView extends AbstractView{

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 可以通过该方法将视图呈现在jsp上等.
     * 此处将其转换为json返回.
     * @param map 也就是model数据
     * @param httpServletRequest
     * @param httpServletResponse
     * @throws Exception
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        //取出connectionStatus方法放入model的.该业务用户的社交帐号绑定状态集合
        Map<String, List<Connection<?>>> connections = (Map<String, List<Connection<?>>>) map.get("connectionMap");

        //我们只需要每个社交帐号是否绑定的信息即可.
        Map<String, Boolean> result = new HashMap<>();
        for (Map.Entry<String, List<Connection<?>>> item : connections.entrySet()) {
            //该map的key是 服务提供商id,例如 qq, weixin
            //值是social帐号关联表中,该业务用户绑定的所有该服务提供商的帐号(一般就一个).
            //所以此处判断其list是否为空,即可知道是否绑定过
            result.put(item.getKey(), !CollectionUtils.isEmpty(item.getValue()));
        }

        //转为json输出
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        httpServletResponse.getWriter().write(objectMapper.writeValueAsString(result));

    }
}
