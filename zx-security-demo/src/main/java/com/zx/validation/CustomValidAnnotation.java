package com.zx.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
