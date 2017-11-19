package com.zx.web.async;

import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017-11-19 16:49
 * 模拟消息队列
 */
@Getter
@Slf4j
@Component
public class MockQueue {
    //下单消息
    private String placeOrder;
    //订单完成消息
    private String completeOrder;
    //下单方法
    public void setPlaceOrder(String placeOrder){
        new Thread(()->{
            log.info("接到下单请求:"+ placeOrder);
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //然后将完成消息赋值,表示该订单已经完成
            this.completeOrder = placeOrder;
            log.info("下单请求处理完毕:" + placeOrder );
        }).start();
    }

    public void setCompleteOrder(String completeOrder) {
        this.completeOrder = completeOrder;
    }
}
