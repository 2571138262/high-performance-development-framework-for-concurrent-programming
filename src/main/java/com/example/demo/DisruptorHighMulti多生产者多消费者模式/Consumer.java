package com.example.demo.DisruptorHighMulti多生产者多消费者模式;

import com.lmax.disruptor.WorkHandler;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumer implements WorkHandler<Order> {

    private String consumerId;
    
    private static AtomicInteger count = new AtomicInteger(0);
    
    private Random random = new Random();

    public Consumer(String consumerId) {
        this.consumerId = consumerId;
    }

    @Override
    public void onEvent(Order event) throws Exception {
        Thread.sleep(1 * random.nextInt(5));
        System.out.println("当前消费者： " + this.consumerId + ",  消费信息ID ：" + event.getId());
        count.incrementAndGet();
    }
    
    public int getCount(){
        return count.get();
    }
}
