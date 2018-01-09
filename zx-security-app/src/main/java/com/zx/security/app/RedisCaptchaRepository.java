package com.zx.security.app;

import com.zx.security.core.validate.CaptchaRepository;
import com.zx.security.core.validate.code.sms.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2018-01-09 21:12
 *
 * 使用redis存储手机验证码
 * 用设备id为key
 */
@Component
public class RedisCaptchaRepository implements CaptchaRepository {

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void put(ServletWebRequest request, String key, Captcha captcha) {
        redisTemplate.opsForValue().set(key + ":" + getDeviceId(request),
                captcha,30, TimeUnit.MINUTES);
    }

    @Override
    public Captcha get(ServletWebRequest request, String key) {
        return (Captcha)redisTemplate.opsForValue().get(key + ":" + getDeviceId(request));
    }

    @Override
    public void remove(ServletWebRequest request, String key) {
        redisTemplate.delete(key + ":" + getDeviceId(request));
    }

    /**
     * 从request中获取设备id
     */
    public String getDeviceId(ServletWebRequest request) {
        String deviceId = request.getHeader("deviceId");
        if (deviceId == null) {
            throw new RuntimeException("请在请求头中携带deviceId");
        }
        return deviceId;

    }
}
