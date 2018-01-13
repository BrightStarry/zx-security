package com.zx.security.social;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionData;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2018-01-11 20:14
 * app模块处理注册时 社交帐号信息存储等操作的工具类
 * See {@link ProviderSignInUtils} 和该工具类作用类似,不过该工具类将信息存在了session中.
 * 并且用设备id作为key
 */
@Component
public class AppSignUpUtils {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    //将 社交用户信息 和 系统用户id 存入关联表
    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    //用来获取ConnectionFactory
    @Autowired
    private ConnectionFactoryLocator connectionFactoryLocator;

    /**
     *  保存社交用户信息
     * @param request
     * @param connectionData 社交登录成功的社交用户信息
     */
    public void saveConnetionData(WebRequest request, ConnectionData connectionData) {
        redisTemplate.opsForValue().set(getKey(request),connectionData,10, TimeUnit.MINUTES);
    }

    public void doPostSignUp( String userId,WebRequest request) {
        //获取 用户社交帐号信息
        String key = getKey(request);
        if (!redisTemplate.hasKey(key)) {
            throw new RuntimeException("无法找到缓存的用户社交帐号信息");
        }
        ConnectionData connectionData = (ConnectionData) redisTemplate.opsForValue().get(key);

        //用ConnectionData 创建 Connection ,以存入关联表
        Connection<?> connection = connectionFactoryLocator
                .getConnectionFactory(connectionData.getProviderId())
                .createConnection(connectionData);
        //在关联表创建对应userId(业务系统id)的记录,并关联对应社交帐号
        usersConnectionRepository.createConnectionRepository(userId).addConnection(connection);

        //完成后删除缓存
        redisTemplate.delete(key);
    }

    private String getKey(WebRequest request) {
        //获取请求头中的设备id
        String deviceId = request.getHeader("deviceId");
        if (StringUtils.isBlank(deviceId)) {
            throw new RuntimeException("设备id参数不能为空");
        }
        return "zx:security:social.connect." + deviceId;
    }
}
