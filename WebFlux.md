#### Spring Boot 2.0

#### bug
* 遇到maven依赖无法找到的bug.基本上将阿里云镜像从setting.xml中去除即可.

#### Web Flux
* Java8 Lambda 函数编程
* Reactive Streams 响应式编程
* Servlet3.1 或 Async NIO(异步非阻塞) 异步编程

* 如果在Spring Boot 2.0导入该模块依赖.默认的容器会从tomcat变为jetty,底层使用的是netty
* 依赖(注意,无法同时添加web和webflux依赖,如果同时添加,会优先使用web.导致webflux路径无效.虽然官方文档上有办法指定,但尝试无果.)  
(虽然没有导入web依赖,但是@PostMapping等标签已经注入)
>
        <dependency>
        	<groupId>org.springframework.boot</groupId>
        	<artifactId>spring-boot-starter</artifactId>
        </dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-webflux</artifactId>
		</dependency>
>

* 使用自定义配置编写接口
>
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
>




