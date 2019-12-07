package com.example.demo.DisruptorHigh高准操作;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;


/**
 * 消费者
 */
public class Handler1 implements EventHandler<Trade>, WorkHandler<Trade> {

    // EventHandler
    @Override
    public void onEvent(Trade event) throws Exception {
        System.out.println("handler 1 : SET NAME ");
        event.setName("H1");
        Thread.sleep(1000);
    }
    
    // WorkHandler
    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }
}
