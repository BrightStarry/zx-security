package com.zx.async.controller;

import com.zx.async.handler.CustomResultHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2017/11/20 0020 09:50
 * 异步控制层
 */
@RestController
@Slf4j
public class AsyncController {

    @GetMapping("/sync")
    public String sync() {
        return "xxxxxx";
    }


    @Autowired
    private MockQueue mockQueue;

    /**
     * ResponseBodyEmitter该对象可以异步的不断的给客户端推送消息
     * 服务端给客户端推送测试
     *
     */
    @RequestMapping("/test2")
    public ResponseBodyEmitter testEmitter() throws InterruptedException {
        //创建异步实体发射器-需要设置超时时间,默认超时时间很短.
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(10000L);


        //只要没有超时前,使用该发射器就可以不断的向客户端写入数据
        Thread success = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    emitter.send("success", MediaType.APPLICATION_JSON_UTF8);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        success.start();



        return emitter;
    }



    //异步测试
    @RequestMapping("/test")
    public DeferredResult<String> test(Long timeout) throws InterruptedException {
        log.info("/test 收到请求");
        //新建延期返回对象并设置超时时间,优先级比configureAsyncSupport方法中默认配置中的高
        DeferredResult<String> result = new DeferredResult<>(timeout);

        //要执行的任务
        Task<String> task = new Task<>(result, "test任务",false);

        //设置超时后执行的任务,优先级比DeferredResultProcessingInterceptor拦截器中的高
        result.onTimeout(()->{
            log.info("任务超时.");
            //告知该任务已经超时-此处应该加锁
            task.setIsTimeout(true);
            result.setErrorResult("任务超时");
        });


        //设置完成时触发的方法
//        result.onCompletion(()->{
//            log.info("onCompletion方法触发");
//        });

        //任务入队
        mockQueue.put(task);
        return  result;
    }
}
