package com.example.demo.DisruptorHigh高准操作;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class TradePublisher implements Runnable {
    
    private Disruptor<Trade> disruptor;
    private CountDownLatch latch;
    private static int PUBLISH_COUNT = 1;
    
    public TradePublisher(CountDownLatch latch, Disruptor<Trade> disruptor) {
        this.latch = latch;
        this.disruptor = disruptor;
    }

    @Override
    public void run() {
        TradeEventTranslator eventTranslator = new TradeEventTranslator();

        for (int i = 0; i < PUBLISH_COUNT; i++) {
            // 新的提交任务的方式
            disruptor.publishEvent(eventTranslator);   
        }
        latch.countDown();
    }
}

class TradeEventTranslator implements EventTranslator<Trade>{
    
    private Random random = new Random();
    
    @Override
    public void translateTo(Trade event, long sequence) {
        this.generateTrade(event);
    }

    private void generateTrade(Trade event) {
        event.setPrice(random.nextDouble() * 9999);
    }
}
