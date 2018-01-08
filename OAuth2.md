#### Spring security OAuth开发app认证框架
* 大致流程
![](image/8.png)


* 此时将demo不依赖browser项目.依赖app项目.以下配置大多在app项目中完成.



#### 认证服务器 启用
* 新建配置类,并启用认证.
>
    /**
     * author:ZhengXing
     * datetime:2018-01-07 21:03
     * 自定义认证服务配置
     */
    @Configuration
    @EnableAuthorizationServer //开启认证服务器
    public class CustomAuthorizationServerConfig {
    }
>

* 然后启动.会发现日志中打印出的接口路径,多了很多/oauth/**的路径



#### 授权码模式授权
* 首先是引导用户跳转登录,获取授权码
>
    
    /oauth/authorize 获取授权码
    参照标准OAuth2协议,可获知所需要的参数和url拼接如下
    127.0.0.1:8080/oauth/authorize?response_type=code&client_id=zx&secret=123456&redirect_uri=http://example.com&scope=all
    
    response_type:规定为"code"字符.
    client_id: 分配给客户的id,启用 Spring security OAuth后,启动时会输出在日志中,也可自行如下配置
        security:
          oauth2:
            client:
              client-id: zx
              client-secret: 123456        
    secret: 密码.同上.
    redirect_uri:回调地址 ,可选(http://example.com 是框架提供的例子页面)
    scope:获取的权限,all
    
    作为一个服务提供商,我们需要知道是 
        1.哪个应用在请求我们:通过client_id 知道
        2.要登录哪个用户:跳转过去输入用户名密码 就知道了
        3.请求什么样的权限:通过scope参数.是我们自己定义的,可以是任意字符.根据该参数分配任意权限
        
    当访问上面的路径,跳转到我们的授权页面,输入用户名密码后,我们会通过UserDetailsService校验.
    (注意,默认情况下,用户需要ROLE_USER这么一个权限才能访问)
>

* 成功跳转后,会进入如下页面,提示用户,是否允许该应用获取它的权限.
![](image/9.png)
点击允许授权后,跳转到第三方应用自己定义的回调页面.我们测试的定义的是一个例子页面.  
并会携带授权码. 例如 http://example.com/?code=PuXL1e

* 此时,第三方应用需要用该授权码请求换取token的url
> /oauth/token
>
    使用postman模拟第三方应用,用授权码换取token.
    1.设置请求头,携带client_id和secret.(点击Authorization,选择basic auth,输入即可自动在header上增加)
        然后再增加 Content-Type : application/x-www-form-urlencoded
    2.设置参数
        1.grant_type:授权类型,固定写 "authorization_code",表示是授权码模式
        2.code:就是返回的授权码
        3.client_id:应用id
        4.redirect_uri:回调地址,照旧测试写http://example.com
        5.scope:和之前的scope传相同值
    3.如果成功的话,会响应
    {
        "access_token": "26626119-0d07-47f8-a8b6-7fed237efd92", 
        "token_type": "bearer", 
        "refresh_token": "32204cdd-0871-4cb0-9a62-9662651fd350",
        "expires_in": 43199,
        "scope": "all"
    }
>

#### 密码模式授权
* 和授权码模式的第二步,授权码换取token类似
>
    使用postman模拟第三方应用,用授权码换取token.
        1.设置请求头,携带client_id和secret.(点击Authorization,选择basic auth,输入即可自动在header上增加)
            然后再增加 Content-Type : application/x-www-form-urlencoded
        2.设置参数
            1.grant_type:授权类型,固定写 "password",表示是密码模式
            2.username:用户名
            3.password:密码
            4.scope:和之前的scope传相同值
        3.如果成功的话,会响应
        {
            "access_token": "26626119-0d07-47f8-a8b6-7fed237efd92",
            "token_type": "bearer",
            "refresh_token": "32204cdd-0871-4cb0-9a62-9662651fd350",
            "expires_in": 42734,
            "scope": "all"
        }
        可以发现,对于同一用户的登录,如果token还没过期.,security框架会响应相同的access_token
>

如果是类似qq登录这样的.用该模式很不适合.因为第三方应用会获取到用户的帐号密码,  
并且无法判断是用户给的,还是其他来源.  
而对于我们写app来说,很适合这种模式.

#### 资源服务器 启用
* 添加如下类即可
>
    /**
     * author:ZhengXing
     * datetime:2018-01-07 21:54
     * 自定义资源服务配置
     */
    @Configuration
    @EnableResourceServer
    public class CustomResourceServerConfig {
    }
>
此时,demo模块,既是认证服务器,又是资源服务器

* 此时,我们假设访问需要认证的 GET /user/me路径,会被告知没有权限
* 我们再次获取一个用户的access_token.
>
    {
        "access_token": "eb715f85-e813-4586-bef0-5a4545248ca0",
        "token_type": "bearer",
        "refresh_token": "a992b7ba-a46f-40ab-8865-8ac55a4eaed6",
        "expires_in": 43199,
        "scope": "all"
    }
>
* 然后再次发起请求,header携带上   
> Authorization : bearer eb715f85-e813-4586-bef0-5a4545248ca0    
(bearer为之前返回的token_type),即可成功


#### SpringSecurityOAuth2核心源码解析
![](image/10.png)
* TokenEndpoint: 接收获取令牌请求
* ClientDetailService:根据client和密码查询是哪个应用.
* ClientDetails:应用信息
* TokenRequest: 请求中其他信息,clientDetails也会在其中
* TokenGranter: 根据OAuth2模式,以及刷新令牌,生成不同的逻辑..
    * OAuth2Request:  ClientDetails和TokenRequest的整合
    * Authentication: 当前授权用户的信息
* OAuth2Authentication: 包含 应用信息/授权用户信息/OAuth2模式/请求参数等各类信息
* AuthorizationServerTokenServices(认证服务器令牌服务): 生成令牌
* OAuth2AccessToken:令牌 

#### 重构框架代码,使其支持 帐号密码/短信验证码/社交 登录三种方式
![](image/11.png)
* 自己重写前面的全部逻辑.
* 其实也就是普通登录成功后,在SuccessHandler中,调用AuthorizationServerTokenServices获取一个accessToken.
* 为了调用该services,需要OAuth2Authentication,而他需要OAuth2Request和Authentication(这个在successHandler中已经构建了)
* OAuth2Request需要使用ClientDetails和TokenRequest构建

* 在app模块的successHandler类中编写
>
     //用来根据client_id查询应用信息,springSecurity默认已经配置好了,直接注入即可
        @Autowired
        private ClientDetailsService clientDetailsService;
        //用来构造令牌的类
        @Autowired
        private AuthorizationServerTokenServices authorizationServerTokenServices;
        /**
         * 当登陆成功时
         *
         * @param request
         * @param response
         * @param authentication 封装了认证信息
         */
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication) throws IOException, ServletException {
            log.info("登录成功");
            /**
             * 解析请求头中的 Authorization
             *
             * header传参的格式(也就是basic auth方式)
             *    "Authorization" : "bearer eb715f85-e813-4586-bef0-5a4545248ca0"
             *    可以从已有的BasicAuthenticationFilter中获取从请求头中获取client_id的代码
             */
            String header = request.getHeader("Authorization");
            //如果没有,或者,不以Basic 开头.
            if (header == null || !header.startsWith("Basic ")) {
                throw new UnapprovedClientAuthenticationException("请求头中无client信息");
            }
    
            String[] tokens = extractAndDecodeHeader(header, request);
            assert tokens.length == 2;
    
            /**
             * 获取ClientDetails并验证
             */
            String clientId = tokens[0];
            String clientSecret = tokens[1];
            //查询应用信息
            ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
            //此处无需做非空判断,因为service中已经做过
            //只需判断密码是否一致
            if (StringUtils.equals(clientDetails.getClientSecret(), clientSecret)) {
                throw new UnapprovedClientAuthenticationException("clientSecret不匹配:" + clientId);
            }
    
            /**
             * 构造
             * 1:请求的参数Map,用来构造Authentication,我们有了,直接传空
             * 4.OAuth2的模式,此处我们是自定义协议,就随便传个custom
             */
            TokenRequest tokenRequest = new TokenRequest(MapUtils.EMPTY_MAP, clientId, clientDetails.getScope(), "custom");
    
            //创建OAuth2Request
            OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);
    
            //创建oAuth2Authentication
            OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request, authentication);
    
            /**
             * 创建token
             */
            OAuth2AccessToken accessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);
    
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            //返回令牌
            response.getWriter().write(objectMapper.writeValueAsString(accessToken));
        }
    
        /**
         * 对 请求头中的client_id 和密码进行Base64解码
         */
        private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
                throws IOException {
    
            byte[] base64Token = header.substring(6).getBytes("UTF-8");
            byte[] decoded;
            try {
                decoded = Base64.decode(base64Token);
            } catch (IllegalArgumentException e) {
                throw new BadCredentialsException(
                        "Failed to decode basic authentication token");
            }
            //解码后的client_id和密码格式为  <client_id>:<密码>
            String token = new String(decoded, "UTF-8");
    
            int delim = token.indexOf(":");
    
            if (delim == -1) {
                throw new BadCredentialsException("Invalid basic authentication token");
            }
            //冒号前为client_id,冒号后为密码
            return new String[]{token.substring(0, delim), token.substring(delim + 1)};
        }
>

* 在app的CustomResourceServerConfig中配置app的安全规则
> CustomResourceServerConfig 详见

* 此时,我们使用postman模拟表单登录
>
    127.0.0.1:8080/login
    选择Authorization的Basic Auth,在header中自动注入client_id和secret
    然后Content-Type : application/x-www-form-urlencoded
    然后在body中附上username和password,
    即可获取到
    {
        "access_token": "b9532f93-0d79-4086-95c5-25f09af8fe91",
        "token_type": "bearer",
        "refresh_token": "0b35ffff-a371-4447-a012-900ad02777d4",
        "expires_in": 43199
    }
    
    然后可以再次模拟之前的用access_token访问/user/me,也就是资源.可以成功获取
>