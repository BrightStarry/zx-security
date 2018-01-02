#### DeferredResult异步对象探究
* 我自己另外建了个模块：zx-async-controller-test
* 组件：
    * controller层方法，接收请求、新建延期返回对象、设置超时方法、任务入队、返回
    * MockQueue队列，任务入队，处理任务、判断是否超时、放入完成队列
    * QueueListener完成队列监听器，监听完成队列，获取到完成的数据后，使用延期对象返回
    * Task任务,三个属性：延期返回对象、任务、是否超时
* 流程：
    1. controller层收到请求,消息入队
    2. 队列监听到消息入队，处理消息
    3. 如果超时任务触发，将超时状态改为true，并直接返回浏览器任务超时
    4. 队列处理完任务后，判断是否超时，如果超时，中断任务，否则将任务加入完成队列
    5. 完成队列监听器监听到消息入队，取出消息，使用延期返回对象返回
* 附加:
    * 使用拦截器处理异步请求
    * 异步请求配置
    * ResponseBodyEmitter对象短时间内主动向客户端推送消息
* 弄了篇博客:  
https://zhuanlan.zhihu.com/p/31223106

#### 异步处理Controller,可增加吞吐量
* 同步代码如下:
>
    @RequestMapping("/order")
    public String order() throws Exception {
        log.info("主线程开始");
        Thread.sleep(1000L);
        log.info("主线程返回");
        return "success";
    }
>

* 使用Callable处理异步请求:
>
    @RequestMapping("/order")
    public Callable<String> order() throws Exception {
        log.info("主线程开始");
        Callable<String> result = new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.info("子线程开启");
                Thread.sleep(5000L);
                log.info("子线程结束");
                return "success";
            }
        };
        log.info("主线程返回");
        return result;
    }
>

* 如下的流程,将无法使用Callable异步处理,
![图片](image/1.png)

* 使用DeferredResult<T>对象处理
    * 概述:
        * controller方法收到一个请求
        * 将内容交给消息队列处理
        * 然后新建一个延期返回对象,并放到集合中保存起来
        * 然后直接返回延期对象,controller的任务就ok了,但此时消息并没有返回
        * 消息队列处理完消息后,放到完成队列
        * 在程序启动时有一个监听器一直监听完成队列
        * 当监听到消息完成后,取出完成消息,并从集合中取出对应的延期对象,并将完成消息赋值给延期对象
        * 延期对象即自动返回
    * 延期返回对象保存器-每一个请求请求到一个异步controller方法,都将产生对应的DeferredResult对象,  
    该类用来保存这些对象:
        >
            /**
             * author:ZhengXing
             * datetime:2017-11-19 16:54
             * 延期返回持有器
             */
            @Component
            @Data
            public class DeferredResultHolder {
                //保存所有延期返回对象
                private Map<String, DeferredResult<String>> map = new HashMap<>();
            }
        >
    * 模拟队列MockQueue-使用setPlaceQueue方法收到消息后,异步处理,然后赋值给订单完成属性:
        >
            public class MockQueue {
                //下单消息
                private String placeOrder;
                //订单完成消息
                private String completeOrder;
                //下单方法
                public void setPlaceOrder(String placeOrder){
                    new Thread(()->{
                        log.info("接到下单请求:"+ placeOrder);
                        try {
                            Thread.sleep(3000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //然后将完成消息赋值,表示该订单已经完成
                        this.completeOrder = placeOrder;
                        log.info("下单请求处理完毕:" + placeOrder );
                    }).start();
                }
        >
    * controller层异步的方法-将消息放入队列,并保存好对应的延期对象后返回即可:
        >
                @Autowired
                private MockQueue queue;
            
                @Autowired
                private DeferredResultHolder deferredResultHolder;
            
                //deferredResult处理异步
                @RequestMapping("/order")
                public DeferredResult<String> order() throws Exception {
                    log.info("主线程开始");
            
                    //生成随机订单号-八位随机数
                    String orderNumber = RandomStringUtils.randomNumeric(8);
                    //放入队列
                    queue.setPlaceOrder(orderNumber);
                    //新建延期返回对象
                    DeferredResult<String> result = new DeferredResult<>();
                    //将延期对象,放入延期对象暂存集合中
                    deferredResultHolder.getMap().put(orderNumber,result);
                    log.info("主线程返回");
                    return result;
                }
        >
    * 模拟监听器-循环监听queue是否有完成消息,如果有,则表示异步处理完成,给延期返回对象设置返回值,即自动返回
        >
            /**
             * author:ZhengXing
             * datetime:2017-11-19 17:04
             * 模拟队列监听器;
             * 该监听器实现ApplicationListener<ContextRefreshedEvent>接口后,可以监听到spring容器启动完成的事件
             */
            @Component
            @Slf4j
            public class QueueListener implements ApplicationListener<ContextRefreshedEvent>{
                @Autowired
                private MockQueue queue;
            
                @Autowired
                private DeferredResultHolder deferredResultHolder;
            
                //当spring容器加载完毕
                @Override
                public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
                    new Thread(()->{
                        while(true){
                            //如果该属性不为空了,表示订单处理完成
                            if(StringUtils.isNotBlank(queue.getCompleteOrder())){
                                //获取完成的订单号
                                String orderNumer = queue.getCompleteOrder();
                                log.info("返回订单处理结果:{}",orderNumer);
                                //获取到该延期对象
                                DeferredResult<String> result = deferredResultHolder.getMap().get(orderNumer);
                                //当该对象调用该方法时,表示已经完成,将会自动返回
                                result.setResult("订单完成");
                                                    
                                //删除相关资源
                                deferredResultHolder.getMap().remove(orderNumer);
                                queue.setCompleteOrder(null);
                            }else{
                                //如果未完成, 暂停,继续
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
            
                            }
                        }
                    }).start();
                }
            }
        >
* 异步配置-不同的拦截器:
>
    @Configuration
    public class WebConfig extends WebMvcConfigurerAdapter{
    
        @Autowired
        private TimeInterceptor timeInterceptor;
    
        //配置异步支持
        //异步请求需要一个和同步请求不同的拦截器DeferredResultProcessingInterceptor
        @Override
        public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
            //设置异步拦截器
    //        configurer.registerDeferredResultInterceptors();
            //设置异步的超时时间
            configurer.setDefaultTimeout(5000L);
            //异步执行时,线程池的配置,spring默认的线程不会重用
    //        configurer.setTaskExecutor();
        }
>

#### swagger使用
1. 引入依赖:
>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.7.0</version>
        </dependency>

        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.7.0</version>
        </dependency>
>
2. 在application类上增加注解:@EnableSwagger2

3. 访问http://127.0.0.1:8080/swagger-ui.html

#### WireMock 模拟后端
1. 官网:http://wiremock.org/docs/running-standalone/  下载jar
2. 使用如下命令启动  
java -jar wiremock-standalone-2.11.0.jar --port 8090
3. 引入依赖-如果不添加第二个依赖会报java.lang.ClassNotFoundException: org.apache.http.HttpEntity异常:
>
        <dependency>
            <groupId>com.github.tomakehurst</groupId>
            <artifactId>wiremock</artifactId>
        </dependency>
        <dependency>
            <groupId>org.apache.httpcomponents</groupId>
            <artifactId>httpmime</artifactId>
        </dependency>
>
4. 新建json文件,写入需要返回的内容
5. 写如下代码直接运行:
>
    /**
     * author:ZhengXing
     * datetime:2017-11-19 18:15
     * 向wireMock传递
     *
     */
    public class MockService {
    
        public static void main(String[] args) throws IOException {
            //设置端口,本机无需设置ip
            WireMock.configureFor(8090);
            //清除之前所有映射
            WireMock.removeAllMappings();
    
            String  url = "/order/1";
            String fileName = "01";
            mock(url, fileName);
    
    
        }
    
        private static void mock(String url, String fileName) throws IOException {
            //读取文件
            ClassPathResource resource = new ClassPathResource("mock/response/"+ fileName+".json");
            String content = FileUtils.readFileToString(resource.getFile(), "utf-8");
            //创建一个接口
            WireMock
                    .stubFor(WireMock.get(WireMock.urlPathEqualTo(url))
                            .willReturn(WireMock.aResponse().withBody(content)
                                    .withStatus(200)));
        }
    }
>


#### 异常处理
* SpringBoot的BasicErrorController类,是其对异常的原始处理,将请求分为text/html和非它两种,分贝返回页面  
和json;它的做法也就是在返回页面的方法上添加如下注解:  
@RequestMapping(produces = {"text/html"})

* 在src/resources目录下新建resources/error/404.html,注意,此时目录为resources/resources/error.  
springboot的404错误将会被转发到该页面

#### 拦截方式
* 顺序: Filter -> Interceptor -> ControllerAdvice -> Aspect -> Controller
* 过滤器
    * 定义过滤器并添加@Compoent注解,但这样无法设置要过滤的路径
    * 定义过滤器,并添加@WebFilter注解,可以设置过滤路径,如下:
        >
            @WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",
                    initParams={
                            @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")// 忽略资源
                    })
        >
    * 定义过滤器,不添加注解,使用如下代码:
        >
                @Configuration
                public class WebConfig{
                    @Bean
                    public FilterRegistrationBean timeFilter() {
                        //创建过滤器注册器
                        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
                        //创建过滤器实例
                        TimeFilter timeFilter = new TimeFilter();
                        //注册
                        filterRegistrationBean.setFilter(timeFilter);
                        //声明过滤器要过滤的url
                        List<String> urlList = new ArrayList<>();
                        urlList.add("/*");
                        filterRegistrationBean.setUrlPatterns(urlList);
                
                        return filterRegistrationBean;
                    }
                }
        >

* 拦截器
    * 配置并注册拦截器:
        >
            @Configuration
            public class WebConfig extends WebMvcConfigurerAdapter{
            
                @Autowired
                private TimeInterceptor timeInterceptor;
            
                //配置拦截器
                @Override
                public void addInterceptors(InterceptorRegistry registry) {
                    registry.addInterceptor(timeInterceptor);
                }
        >
    * 注意,拦截器的afterCompletion()方法的第三个参数可以接收到controller层的异常,但如果自定义了异常处理器,  
    也就是@ExceptionHandler,该方法的该参数将无法捕获到异常,将为null

* 切片(Aspect)
    * 导入依赖:
        >
                <dependency>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-aop</artifactId>
                </dependency>    
        >
    * 如下配置:
        >
            /**
             * author:ZhengXing
             * datetime:2017-11-19 15:33
             * 自定义切片AOP
             *
             * aop时间:
             * @After 之后
             * @Before 之前
             * @AfterThrowing 抛出异常后
             * @Around 包含上面三个注解
             */
            @Aspect
            @Component
            public class TimeAspect {
                //拦截任何返回值的userController中任何方法任何参数
                @Around("execution(* com.zx.web.controller.UserController.*(..))")
                public Object handleControllerMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
            
                    System.out.println("time aspect start");
                    
                    //获取方法所有参数
                    Object[] params = proceedingJoinPoint.getArgs();
                    for (Object param : params) {
                        System.out.println("param is " + param );
                    }
            
                    long startTime = new Date().getTime();
                    //执行真正的方法
                    Object result = proceedingJoinPoint.proceed();
                    System.out.println("time aspect 耗时:" + (new Date().getTime() - startTime));
                    System.out.println("time aspect end");
                    return result;
                }
            }
        >

#### 文件
* 上传:
>
    @PostMapping
    public FileInfo upload(MultipartFile file) throws Exception {
        System.out.println(file.getName());
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());

        //存放的文件夹
        String folder = "H:\\ideaworkspace\\zx-security\\zx-security-demo\\src\\main\\java\\com\\zx\\web";
        File localFile = new File(folder, new Date().getTime() + ".txt");
        //将传入的文件写入本地,其实现原理还是FileCopyUtils
        //如果不写入本地,可以使用file.getInputStream()获取流
        file.transferTo(localFile);

        //返回上传的文件信息
        return new FileInfo(localFile.getAbsolutePath());
    }
>
* 上传测试:
>
    //文件上传
    @Test
    public void whenUploadSuccess() throws Exception {
        String result = mockMvc.perform(MockMvcRequestBuilders.fileUpload("/file")
                .file(
                        new MockMultipartFile("file",//方法中接收文件的参数名
                                "test.txt",//文件名
                                MediaType.MULTIPART_FORM_DATA_VALUE,//类型
                                "hello test".getBytes("UTF-8")//文件内容
                        )
                ))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(result);
    }
>
* 下载:
>
        @GetMapping("/{id}")
        public void download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
            try (InputStream inputStream = new FileInputStream(new File(folder, id + ".txt"));
                 OutputStream outputStream = response.getOutputStream()) {
                //定义类型和下载过去的文件名
                response.setContentType("application/x-download");
                response.addHeader("Content-Disposition","attachment;filename=test.txt");

                //将输入流输出到输出流
                IOUtils.copy(inputStream, outputStream);
                outputStream.flush();
            }
        }
>

#### hibernate Validation 自定义校验注解
1. 自定义注解:
>
    /**
     * author:ZhengXing
     * datetime:2017-11-19 13:51
     * <p>
     * 自定义校验注解
     * <p>
     * 如下指定了该校验注解类的校验器
     * @Constraint(validatedBy = CustomValidator.class)
     *
     * 该注解的三个属性是从@NotBlank拷贝过来的
     */
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @Constraint(validatedBy = CustomValidator.class)
    public @interface CustomValidAnnotation {
        String message();
    
        Class<?>[] groups() default {};
    
        Class<? extends Payload>[] payload() default {};
    }
>

2. 自定义校验器
>
    /**
     * author:ZhengXing
     * datetime:2017-11-19 13:54
     * 自定义校验处理类
     * 第一个泛型是 自定义的验证注解类
     * 第二个泛型是 要验证的那个字段的字段类型
     *
     * 注意:该类无需@Compoent等注解,在其实现该接口的时候,已经加入bean
     * 可以注入一些service,进行一些注入某个id是否在数据库中存在的校验
     */
    public class CustomValidator implements ConstraintValidator<CustomValidAnnotation,Object> {
        //校验器初始化
        @Override
        public void initialize(CustomValidAnnotation constraintAnnotation) {
            System.out.println("校验器初始化");
        }
    
        //校验
        @Override
        public boolean isValid(Object value, ConstraintValidatorContext context) {
            System.out.println(value);
            return true;
        }
    }
>****