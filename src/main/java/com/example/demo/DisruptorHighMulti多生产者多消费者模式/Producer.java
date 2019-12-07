package com.example.demo.DisruptorHighMulti多生产者多消费者模式;

import com.lmax.disruptor.RingBuffer;

public class Producer {

    private RingBuffer<Order> ringBuffer;

    public Producer(RingBuffer<Order> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void sendData(String data) {
        long sequence = ringBuffer.next();
        try{
            Order order = ringBuffer.get(sequence);
            order.setId(data);
        }finally {
            ringBuffer.publish(sequence);
        }
    }
}
