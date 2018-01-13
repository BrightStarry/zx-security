package com.zx.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.zx.dto.User;
import com.zx.dto.UserQueryCondition;
import com.zx.security.core.properties.SecurityProperties;
import com.zx.security.social.AppSignUpUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017-11-18 23:51
 * 用户
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
//    private ProviderSignInUtils providerSignInUtils;

//    @Autowired
    private AppSignUpUtils appSignUpUtils;

    @Autowired
    private SecurityProperties securityProperties;


    /**
     * 注册
     */
    @PostMapping("/register")
    public void register(User user, HttpServletRequest request) {
        //注册或是绑定用户逻辑
        //成功后都会拿到用户唯一标识
        //此处暂时用 用户名作为唯一标识
        String userId = user.getUsername();

        //将用户唯一标识 传递给 social, social通过request从session中取出第三方用户信息
        //和该唯一标识一起插入到  它自带的 关联表中.
//        providerSignInUtils.doPostSignUp(userId,new ServletWebRequest(request));
        appSignUpUtils.doPostSignUp(userId,new ServletWebRequest(request));
    }




    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable String id) {
        System.out.println(id);
    }


    /**
     * 获取用户信息
     *
     * 原先是从session中取出security框架定义的登录用户信息
     * 现在替换为解析jwt令牌,并获取其中的自定义信息
     */
    @SneakyThrows
    @GetMapping("/me")
    public Object getCurrentUser(Authentication user,HttpServletRequest request) {
        //获取应用访问携带的jwt格式的access_token
        //例如bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJ6eCIsInNjb3BlIjpbInJlYWQiXSwiZXhwIjoxNTE1ODE3NDczLCJhdXRob3JpdGllcyI6WyJhZG1pbiIsIlJPTEVfVVNFUiJdLCJqdGkiOiIwZjRkZjMxZS0yNjNlLTRlNTEtYWY0MC04ZmQ5ZjkxN2FlMWYiLCJhZ2UiOjIyLCJjbGllbnRfaWQiOiJ6eCJ9.tM9MmC1mJiku-CUNxi2x4n4FIWNOZoW7gbKtE8a1Vyw
        //bearer后面为jwt
        String authorization = request.getHeader("Authorization");
        String token = StringUtils.substringAfter(authorization, "bearer ");

        Claims claims = Jwts.parser()
                //设置之前的签名.
                //此处是解析签名,验证是否被篡改; 但签名时是使用的spring的jwtAccessTokenConverter类,是使用UTF-8编码的.
                //所以此处也要指定编码
                .setSigningKey(securityProperties.getOauth2().getJwtSigningKey().getBytes("UTF-8"))
                .parseClaimsJws(token).getBody();//将其解析为了一个对象,方便获取

        //取出自定义的age字段
        Integer age = (Integer) claims.get("age");
        log.info("自定义字段为:{}", age);

        return user;
    }

    /**
     * 修改用户
     * @param id
     * @param user
     * @param errors
     * @return
     */
    @PutMapping("/{id:\\d+}")
    public User update(@PathVariable String id,
                       @Valid @RequestBody User user,
                       BindingResult errors) {
        if(errors.hasErrors()){
            errors.getAllErrors().parallelStream().forEach(error->{
                //可以将error转为FieldError,获取一些属性的信息
//                FieldError fieldError = (FieldError) error;
//                System.out.println(fieldError.getField() + ":" + fieldError.getDefaultMessage());
                System.out.println(error.getDefaultMessage());
            });
        }
        System.out.println(user);
        user.setId("1");
        return user;
    }


    /**
     * 创建用户
     * @param user
     * @param errors
     * @return
     */
    @PostMapping()
    public User create(@Valid @RequestBody User user, BindingResult errors) {
        if(errors.hasErrors()){
            errors.getAllErrors().parallelStream().forEach(error->{
                System.out.println(error.getDefaultMessage());
            });
        }
        System.out.println(user);
        user.setId("1");
        return user;
    }



    /**
     * 查询用户列表
     * @param condition
     * @param pageable
     * @return
     */
    @JsonView(User.UserSimpleView.class)
    @GetMapping("")
    public List<User> query(UserQueryCondition condition,
                            @PageableDefault(size = 10,page = 0,sort = "username,asc") Pageable pageable) {
        System.out.println(ReflectionToStringBuilder.toString(condition, ToStringStyle.MULTI_LINE_STYLE));
        return Arrays.asList(new User(), new User(), new User());
    }


    /**
     * 查询用户详情
     * @param id
     * @return
     */
    @ApiOperation(value = "用户查询服务")
    @JsonView(User.UserDetailView.class)
    @GetMapping("/{id:\\d+}")
    public User getInfo(@PathVariable String id) {
//        throw new UserNotExistException("1");
        System.out.println("调用getInfo服务");
        User user = new User("2","zx","11111",new Date());
        return user;
    }
}
