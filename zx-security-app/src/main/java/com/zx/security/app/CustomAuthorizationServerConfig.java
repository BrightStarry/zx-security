package com.zx.security.app;

import com.zx.security.core.properties.SecurityProperties;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.LinkedList;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-01-07 21:03
 * 自定义认证服务配置
 *
 * AuthorizationServerConfigurerAdapter有三个config方法,
 * 分别针对ServerEndpoints/Server/Client的配置
 *
 * 我们使用自定义的该配置后,默认的OAuth2AuthorizationServerConfiguration不再生效,所以需要自行注入一些类
 *
 * 如下是默认的该配置类
 * See {@link org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration}
 */
@Configuration
@EnableAuthorizationServer //开启认证服务器
public class CustomAuthorizationServerConfig extends AuthorizationServerConfigurerAdapter{
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenStore redisTokenStore;

    @Autowired(required = false)
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired(required = false)
    private TokenEnhancer jwtTokenEnhancer;

    /**
     * 配置TokenEndpoint
     *
     * 将它需要的两个对象注入.因为OAuth2AuthorizationServerConfiguration被我们这个类替换了
     *
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenStore(redisTokenStore);

        //启用jwt时,注入
        if (jwtAccessTokenConverter != null && jwtTokenEnhancer != null) {
            //增强器链
            TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
            List<TokenEnhancer> enhancers = new LinkedList<>();
            enhancers.add(jwtTokenEnhancer);//用于在令牌中追加信息
            enhancers.add(jwtAccessTokenConverter);//用于将普通的UUID的令牌转为jwt令牌
            tokenEnhancerChain.setTokenEnhancers(enhancers);

            endpoints
                    .tokenEnhancer(tokenEnhancerChain)//注入增强器链
                    .accessTokenConverter(jwtAccessTokenConverter);//设置存取token转换器,
        }
    }
    /**
     * 配置 第三方应用 client
     *
     * 配置它后,yml中的security.oauth2.client失效
     *
     * 可以将这些参数也放到外部配置中,此处不做操作
     * 例如将yml配置注入到list中,然后在此处循环list.就可以构建多个client
     *
     * 此处如果循环时抛出异常,不做处理,停止程序
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        InMemoryClientDetailsServiceBuilder builder = clients.inMemory();//使用内存存储
        securityProperties.getOauth2().getClients().forEach(client -> {
            try {

                builder.withClient(client.getClientId())//使用该client
                        .secret(client.getClientSecret())//密钥
                        .accessTokenValiditySeconds(client.getAccessTokenValiditySeconds())//令牌有效时间,为0时,不过期
                        .authorizedGrantTypes(client.getAuthorizedGrantTypes().toArray(new String[0]))//允许该应用使用的授权模式s , refresh_token刷新令牌, password密码模式
//                        .refreshTokenValiditySeconds()//刷新令牌后的过期时间
                        //有哪些可用权限,随便定义字符.该应用请求时携带的scope必须在该数组中; 配置后,请求时也可以不携带scope,自动有其所有scope
                        .scopes(client.getScopes().toArray(new String[0]));
            } catch (Exception e) {
                throw new RuntimeException("oauth2 client 配置异常", e);
            }
        });
    }
}
