package com.zx.async.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * author:ZhengXing
 * datetime:2017/11/20 0020 09:57
 * 模拟队列
 */
@Component
@Slf4j
public class MockQueue {
    //接收队列
    private BlockingQueue<Task<String>> receiveQueue = new LinkedBlockingQueue<>();
    //完成队列
    private BlockingQueue<Task<String>> completeQueue = new LinkedBlockingQueue<>();

    //接收任务
    void put(Task<String> task) throws InterruptedException {
        receiveQueue.put(task);
    }

    //获取完成任务
    Task<String> get() throws InterruptedException {
        return completeQueue.take();
    }

    //处理任务
    private void execute() {
        new Thread(()->{
            while (true) {
                try {
                    //从接收队列中取出任务，处理，然后放入成功队列
                    Task<String> task = receiveQueue.take();
                    log.info("队列收到数据,处理中");
                    Thread.sleep(2000L);
                    task.setMessage("success");

                    //如果超时了,中断该任务-此处应该加锁
                    if(task.getIsTimeout()){
                        log.info("任务超时。处理线程中断该任务");
                        continue;
                    }

                    log.info("队列处理完成");
                    completeQueue.put(task);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //开启处理任务
    public MockQueue() {
        execute();
    }
}
