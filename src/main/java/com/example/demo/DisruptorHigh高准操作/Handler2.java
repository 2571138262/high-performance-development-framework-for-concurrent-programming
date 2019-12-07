package com.example.demo.DisruptorHigh高准操作;


import com.lmax.disruptor.EventHandler;

import java.util.UUID;

/**
 * 消费者
 */
public class Handler2 implements EventHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        Thread.sleep(2000);
        System.out.println("handler 2 : SET ID ");
        event.setId(UUID.randomUUID().toString());
    }
}
