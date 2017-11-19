package com.zx.web.async;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.Callable;

/**
 * author:ZhengXing
 * datetime:2017-11-19 16:34
 * 异步控制器
 */
@RestController
@Slf4j
public class AsyncController {
    @Autowired
    private MockQueue queue;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    //deferredResult处理异步
    @RequestMapping("/order")
    public DeferredResult<String> order() throws Exception {
        log.info("主线程开始");

        //生成随机订单号-八位随机数
        String orderNumber = RandomStringUtils.randomNumeric(8);
        //放入队列
        queue.setPlaceOrder(orderNumber);
        //新建延期返回对象
        DeferredResult<String> result = new DeferredResult<>();
        //将延期对象,放入延期对象暂存集合中
        deferredResultHolder.getMap().put(orderNumber,result);
        log.info("主线程返回");
        return result;
    }


    //Callable处理异步
//    @RequestMapping("/order")
//    public Callable<String> order() throws Exception {
//        log.info("主线程开始");
//        Callable<String> result = new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                log.info("子线程开启");
//                Thread.sleep(5000L);
//                log.info("子线程结束");
//                return "success";
//            }
//        };
//        log.info("主线程返回");
//        return result;
//    }
}
