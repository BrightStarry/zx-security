package com.zx.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.zx.validation.CustomValidAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Past;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017-11-18 23:55
 * 用户
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    //用户简单视图
    public interface UserSimpleView {};

    //用户详情视图
    public interface  UserDetailView extends UserSimpleView {};

    @JsonView(UserSimpleView.class)
    private String id;

    //将该属性在简单视图展示
    @CustomValidAnnotation(message = "名字")
    @JsonView(UserSimpleView.class)
    private String username;

    //将密码属性在详情视图才展示,
    // 但是详情视图仍然会显示简单视图的属性,因为有一个继承关系
    @NotBlank(message = "密码不能为空")
    @JsonView(UserDetailView.class)
    private String password;

    @Past(message = "生日必须是过去")
    @JsonView(UserSimpleView.class)
    private Date birthday;
}
