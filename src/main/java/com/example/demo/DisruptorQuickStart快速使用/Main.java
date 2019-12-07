package com.example.demo.DisruptorQuickStart快速使用;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        
        // 参数准备工作
        int ringBufferSize = 1024 * 1024;
        ExecutorService exec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        /**
         *      1、eventFactory ： 消息(Event)工厂对象    
         *      2、ringBufferSize ：容器的长度
         *      3、executor ：线程池  (建议使用自定义线程池) 线程数量、拒绝策略 RejectedExecutionHandler
         *      4、ProducerType ： 单生产者还是多生产者
         *      5、等待策略 ： waitStrategy 等待策略 
         */
        // 1、实例化Disruptor对象
        Disruptor<OrderEvent> disruptor = new Disruptor<>(
                new OrderEventFactory(),
                ringBufferSize,
                exec,
                ProducerType.SINGLE, // Disruptor 支持多生产者模式和单生产者模式
                new BlockingWaitStrategy() // 阻塞的等待策略
        );
        
        // 2、添加消费者的监听 (Disruptor 与 消费者 的一个关联关系)
        disruptor.handleEventsWith(new OrderEventHandler());
        
        // 3、启动Disruptor
        disruptor.start();
        
        // 4、获取实际存储数据的容器 ： RingBuffer
        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
        
        // 5、生产一个生产者对象
        OrderEventProducer producer = new OrderEventProducer(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);

        for (int i = 0; i < 100; i++) {
            bb.putLong(0, i);
            producer.sendData(bb);
        }
        
        disruptor.shutdown();
        exec.shutdown();
        
        
    }
    
}
