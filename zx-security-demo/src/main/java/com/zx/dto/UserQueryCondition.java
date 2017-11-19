package com.zx.dto;

import lombok.Data;

/**
 * author:ZhengXing
 * datetime:2017-11-19 0:06
 * 用户查询请求对象
 */
@Data
public class UserQueryCondition {

    private String username;

    private Integer age;

    private Integer ageTo;

    private String xxx;
}

