package com.zx.security.app;

import com.zx.security.core.social.CustomSpringSocialConfigurer;
import com.zx.security.core.social.SocialConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2018-01-11 20:41
 * 实现BeanPostProcessor接口后.spring所有bean在初始化前后都需要经过该类处理
 *
 * 该类的作用就是为了 当使用app的时候 修改 See{@link SocialConfig#zxSocialSecurityConfig()}
 * 这个bean的注册url路径 为一个我们指定的新路径.用来处理注册逻辑
 *
 */
@Component
public class SpringSocialConfigurerPostProcessor implements BeanPostProcessor {
    /**
     * 初始化前,不做任何操作
     * @param bean
     * @param beanName
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 初始化完成后,处理的方法
     * @param bean
     * @param beanName
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (StringUtils.equals(beanName, "zxSocialSecurityConfig")) {
            //转为原来的类
            CustomSpringSocialConfigurer configurer = (CustomSpringSocialConfigurer) bean;
            configurer.signupUrl("/social/signUp");
            return configurer;
        }
        return bean;
    }
}
