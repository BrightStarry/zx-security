package com.zx.async.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * author:ZhengXing
 * datetime:2017/11/20 0020 10:02
 * 任务，保存DeferredResult和任务内容
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task<T> {
    //延时返回对象
    private DeferredResult<String> result;
    //任务消息
    private T message;
    //是否超时
    private Boolean isTimeout;

}
