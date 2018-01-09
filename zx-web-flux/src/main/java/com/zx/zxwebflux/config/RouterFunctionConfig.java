package com.zx.zxwebflux.config;

import com.zx.zxwebflux.dto.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.ws.Response;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2018-01-09 21:48
 * 定义路由器函数 配置类
 * 类似@RequestMapping
 */
@Configuration
@EnableWebFlux
public class RouterFunctionConfig {
    /**
     * Servlet
     *  请求接口:ServletRequest或HttpServletRequest
     *  响应接口:ServletResponse或HttpServletResponse
     * Spring5.0 重新定义了服务请求和响应接口
     *  请求接口:ServerRequest
     *  响应接口:ServerResponse
     *  既可以支持Servlet规范,也可以支持自定义,例如Netty Web Server
     * 本例中,
     *  定义get请求,定义测试URI: /test
     *  Flux是 0 - N个对象集合
     *  Mono是 0 - 1个对象集合
     *  Reactive中的Flux或者Mono是异步处理
     *  JDK普通集合对象基本上都是同步处理
     *  Flux或者Mono都是Publisher
     */
    @Bean
    public RouterFunction<ServerResponse> test() {
        return RouterFunctions.route(RequestPredicates.GET("/a"),
                serverRequest -> {
                    //要返回的集合对象
                    Collection<User> users = Arrays.asList(new User("a", 1), new User("b", 2));
                    //将返回对象转为Flux
                    Flux<User> userFlux = Flux.fromIterable(users);
                    return ServerResponse.ok().body(userFlux, User.class);
                });
    }
}
