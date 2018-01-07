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