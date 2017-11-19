package com.zx.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

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
