package com.zx.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.zx.dto.User;
import com.zx.dto.UserQueryCondition;
import com.zx.exception.UserNotExistException;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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
public class UserController {

    @DeleteMapping("/{id:\\d+}")
    public void delete(@PathVariable String id) {
        System.out.println(id);
    }


    /**
     * 获取用户信息
     */
    @GetMapping("/me")
    public Object getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        //一种方法是自己获取
        //SecurityContextHolder.getContext().getAuthentication()

        //第二种方式
        //直接在方法参数中写Authentication,即可获取

        //第三种,只想获取Authentication中的UserDetails
        //在方法参数中这么写@AuthenticationPrincipal UserDetails user

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
