package com.example.demo.DisruptorHigh高准操作;

import com.lmax.disruptor.EventHandler;

/**
 * 消费者
 */
public class Handler5 implements EventHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println("handler 5 : GET PRICE : " + event.getPrice());
        event.setPrice(event.getPrice() + 3);
    }
}
