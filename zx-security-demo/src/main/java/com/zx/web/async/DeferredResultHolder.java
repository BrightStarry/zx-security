package com.zx.web.async;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.HashMap;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017-11-19 16:54
 * 延期返回持有器
 */
@Component
@Data
public class DeferredResultHolder {
    //保存所有延期返回对象
    private Map<String, DeferredResult<String>> map = new HashMap<>();
}
