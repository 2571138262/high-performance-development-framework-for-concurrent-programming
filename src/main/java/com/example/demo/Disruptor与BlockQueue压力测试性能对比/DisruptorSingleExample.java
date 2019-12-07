package com.example.demo.Disruptor与BlockQueue压力测试性能对比;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.Executors;

/**
 * 使用Disruptor并发框架
 */
public class DisruptorSingleExample {

    public static void main(String[] args) {
        int ringBufferSize = 65536;
        final Disruptor<Data> disruptor = new Disruptor<>(
                () -> new Data(),
                ringBufferSize,
                Executors.newSingleThreadExecutor(),
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
        );
        
        DataConsumer consumer = new DataConsumer();
        // 消费数据
        disruptor.handleEventsWith(consumer);
        disruptor.start();
        new Thread(() -> {
            RingBuffer<Data> ringBuffer = disruptor.getRingBuffer();
            for (Long i = 0L; i < Constants.EVENT_NUM_OHM; i++) {
                long seq = ringBuffer.next();
                Data data = ringBuffer.get(seq);
                data.setId(i);
                data.setName("c" + i);
                ringBuffer.publish(seq);
            }
        }).start();
    }
    
}
