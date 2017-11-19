package com.zx.web.async;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * author:ZhengXing
 * datetime:2017-11-19 17:04
 * 模拟队列监听器;
 * 该监听器实现ApplicationListener<ContextRefreshedEvent>接口后,可以监听到spring容器启动完成的事件
 */
@Component
@Slf4j
public class QueueListener implements ApplicationListener<ContextRefreshedEvent>{
    @Autowired
    private MockQueue queue;

    @Autowired
    private DeferredResultHolder deferredResultHolder;

    //当spring容器加载完毕
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        new Thread(()->{
            while(true){
                //如果该属性不为空了,表示订单处理完成
                if(StringUtils.isNotBlank(queue.getCompleteOrder())){
                    //获取完成的订单号
                    String orderNumer = queue.getCompleteOrder();
                    log.info("返回订单处理结果:{}",orderNumer);
                    //获取到该延期对象
                    DeferredResult<String> result = deferredResultHolder.getMap().get(orderNumer);
                    //当该对象调用该方法时,表示已经完成,将会自动返回
                    result.setResult("订单完成");


                    //删除相关资源
                    deferredResultHolder.getMap().remove(orderNumer);
                    queue.setCompleteOrder(null);
                }else{
                    //如果未完成, 暂停,继续
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
}
