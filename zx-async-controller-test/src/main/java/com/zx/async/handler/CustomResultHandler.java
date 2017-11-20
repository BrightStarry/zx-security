package com.zx.async.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * author:ZhengXing
 * datetime:2017/11/20 0020 13:29
 * 延期返回对象返回值 处理器
 */
@Slf4j
@Component
public class CustomResultHandler implements DeferredResult.DeferredResultHandler {
    /**
     * 处理返回值
     * @param result
     */
    @Override
    public void handleResult(Object result) {
        log.info("CustomResultHandler触发");
    }
}
