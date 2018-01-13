package com.zx.security.app;

import com.zx.security.app.jwt.CustomJwtTokenEnhancer;
import com.zx.security.core.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * author:ZhengXing
 * datetime:2018-01-12 19:24
 * 配置redis存储token
 *
 * 以及jwt配置
 *
 * 两个@ConditionalOnProperty注解的效果是,
 * 未配置storeType,使用jwt,
 * 配置了,并指定了对应的类型,就使用对应的
 */
@Configuration
public class TokenStoreConfig {
    //redis连接工厂
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;



    /**
     * 创建security帮我们构建好的 使用redis 作为 tokenStore的类
     * 为了注入redisTokenStore这个bean.
     * 让我们服务端的令牌存储到redis中,而非内存中.
     */
    @Bean
    @ConditionalOnProperty(prefix = "zx.security.oauth2",name = "storeType",havingValue = "redis")
    public TokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }


    /**
     * jwt 配置类
     */
    @Configuration
    @ConditionalOnProperty(prefix = "zx.security.oauth2",//属性前缀
            name = "storeType",//属性
            havingValue = "jwt",//预期值
            matchIfMissing = true//指定属性如果没有设置,条件是否匹配,为true,则没有设置属性时,注入bean
            //注意,是属性是否设置,而不是是否是该值.如果没设置,注入该bean
    )
    public static class JwtTokenConfig {

        @Autowired
        private SecurityProperties securityProperties;

        /**
         * jwt令牌存储
         */
        @Bean
        public TokenStore jwtTokenStore() {
            return new JwtTokenStore(jwtAccessTokenConverter());
        }

        /**
         * jwt令牌生成
         */
        @Bean
        public JwtAccessTokenConverter jwtAccessTokenConverter() {
            JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
            //签名 防篡改 用的密钥
            jwtAccessTokenConverter.setSigningKey(securityProperties.getOauth2().getJwtSigningKey());
            return jwtAccessTokenConverter;
        }

        /**
         *  jwt令牌增强器
         *  用于在jwt中增加自定义信息
         *
         *  业务系统可以通过自定义该类,来覆盖该bean
         */
        @Bean
        @ConditionalOnMissingBean(name = "jwtTokenEnhancer")
        public TokenEnhancer jwtTokenEnhancer() {
            return new CustomJwtTokenEnhancer();
        }
    }
}
