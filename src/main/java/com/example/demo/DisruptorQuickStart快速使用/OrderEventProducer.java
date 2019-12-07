package com.example.demo.DisruptorQuickStart快速使用;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * 消息数据生产者, 用于投递数据
 */
public class OrderEventProducer {
    
    // 实际存储数据的容器
    private RingBuffer<OrderEvent> ringBuffer;

    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }
    
    public void sendData(ByteBuffer data){
        // 1、在生产者发送消息的时候首先需要从ringBuffer里获取一个可用的序号
        long sequence = ringBuffer.next();
        try {
            // 2、根据这个需要找到具体的 "orderEvent" 元素, 注意：此时获取的OrderEvent对象是一个没有填充的(属性没有被赋值)
            OrderEvent event = ringBuffer.get(sequence);
            // 3、进行实际的赋值处理
            event.setValue(data.getLong(0));    
        }finally {
            // 4、提交发布操作 
            ringBuffer.publish(sequence);   
        }
    }
}
