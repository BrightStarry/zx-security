package com.zx.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * author:ZhengXing
 * datetime:2017-11-19 14:34
 * 自定义异常
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserNotExistException extends RuntimeException {
    private String id;

    public UserNotExistException(String id) {
        super("user not exist");
        this.id = id;
    }
}
