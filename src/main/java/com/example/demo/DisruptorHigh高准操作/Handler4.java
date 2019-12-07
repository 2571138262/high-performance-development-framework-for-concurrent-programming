package com.example.demo.DisruptorHigh高准操作;

import com.lmax.disruptor.EventHandler;

/**
 * 消费者
 */
public class Handler4 implements EventHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("handler 4 : SET PRICE");
        event.setPrice(17.0);
    }
}
