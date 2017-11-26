package com.zx.web.controller;

import com.zx.exception.UserNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017-11-19 14:37
 * 异常处理器
 */
@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UserNotExistException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleUserNotExistException(UserNotExistException e) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("id", e.getId());
        resultMap.put("message", e.getMessage());
        return resultMap;
    }

    @ExceptionHandler(Exception.class)
    public String handler(Exception e, HttpServletRequest request) {
        request.setAttribute("error",e.getMessage());
        return "forward:/error/custom";
    }
}
