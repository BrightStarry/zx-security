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

* 对于图形验证码和短信验证码登录.在普通表单登录成功后基本应该也能成功.  
但是因为app中不再使用session.所以.需要更换缓存验证码的逻辑.
    * 在core中创建CaptchaRepository接口.定义保存/删除/获取 验证码的方法,
    * 将之前使用session保存/删除/获取验证码的代码,全部用该接口替换.
    * 在app和browser中分别创建使用redis和session的实现类.即可.
    * 测试手机号登录. 使用postman先发起获取验证码请求.注意携带deviceId(设备id,用于作为key)
    * 然后再发起.登录请求,注意使用Basic Auth携带client_id和密码.即可


* 重构社交登录.
    * 社交登录在使用app后,会通过qq等服务提供商提供的SDK进行授权验证,其流程如下所示
    ![](image/12.png)
    * 也可能是简化模式
    ![](image/13.png)
    * 无论是简化模式,还是标准模式,当其完成,获取到qq的access_token后,是可以获取到例如qq的用户信息等.  
        但对于我们自己的项目来说,该用户还是没有登录(我们没有给它access_token).
    * 我们就可以实现,让社交登录的用户,用它获取到的open_id换取我们的令牌.(一个问题,open_id是不变的.用户信息安全如何保证)
        其逻辑和手机验证码登录是类似的.
    * 如下
    >
        1. 构造OpenIdAuthenticationToken类封装登录请求(openId和providerId),和登录成功后的用户信息
        2. 构造OpenIdAuthenticationFilter类过滤自定义的一个社交登录请求,获取到对应请求属性.
        3. 构造OpenIdAuthenticationProvider类,进行自定义的身份校验.
        4. 构造配置类,将以上的过滤器及相关类,注入到security过滤器链后.
    >
    * 然后测试的时候使用Basic Auth携带providerId和openId.即可(该参数从之前第三方登录成功后的关联表记录中获取测试)
    * 如此就是使用了简化模式获取access_token
    * 如果是标准模式的app社交登录.我们之前的代码都有过对应逻辑.  
        但之前qq之类的服务提供商是将授权码请求发给我们后台的,此时发给了app.那么app需要将该请求一模一样的转发给我们即可.
    >
        详细说明下,之前是我们访问 /oauth/qq 这样的请求进行qq登录.因为此时code授权码不存在,social过滤器会将用户
        导向qq的登录界面.而登录成功后.qq同样会回调该请求.并携带授权码.此时social就会拿着这个授权码去换取令牌.
        此时,无非是app帮我们做了前面的步骤.并帮我们接收到了授权码回调请求.那么.他只要将该请求参数原封不动的发给
        我们的/oauth/qq路径即可.
        
        
        但是.在broswer中.获取到qq令牌后的策略是跳转页面.而此时需要返回该令牌.
        所以定义如下处理器接口
        SocialAuthenticationFilterPostProcessor.
        并在CustomSpringSocialConfigurer中额外增加.如果该处理器不为空,调用处理方法
        然后在SocialConfig类中,注入CustomSpringSocialConfigurer时,设置SocialAuthenticationFilterPostProcessor.
        最后再在app模块定义了一个处理器接口实现,其实就是将app模块的自定义的成功处理器set给了那个过滤器.让其调用我们自定义的成功处理器.
        
        此处不再测试.如果测试.可以先启用broswer模块.在获取到的授权码回调的地方.debug.获取到授权码.然后再开app
        应用.用postman模拟请求即可(get方式,code=xxx).
    >
* 此处,重洗梳理下app社交登录流程.
    * 首先.我们定义了一个登录方式,就是用openId和providerId换取我们自己的access_token    
    * 如果是简化模式.app可以直接获取到openId.直接调用我们的登录方式即可.
    * 如果是标准模式.app在获取到授权码后,需要将其发给我们的社交登录接口
    (例如/oauth/qq,也就是我们浏览器模块中,用户点击来跳转到qq登录的那个URI).该请求需要携带Basic Auth
    (也就是app作为我们(我们此时是服务提供商)的第三方应用的client_id和密码),然后我们通过该授权码获取到
    qq的access_token后.返回给app.然后app再请求我们新定义的openId的登录方式即可.

* 我本来懒得测试标准模式了的.但强迫症让我去测试了一下.
>
    结果因为一个bug搞到了现在(2018年1月10日 23:59:26)
    其表现为,postman模拟发送请求码后,一直未调用successHandler.直接返回注册页面.
    我一个一个断点分析过去..最后发现其原因只是UsersConnectionRepository类,查询social关联表时
    查到了两条记录(这两条记录的providerId和openId相同,是我测试微信登录时使用了不同的业务系统用户名产生的..),
    然后就返回了null(只允许有一条),然后就跳转到了注册页面...删除一条记录后即可.
>