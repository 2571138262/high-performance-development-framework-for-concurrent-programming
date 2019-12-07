package com.example.demo.DisruptorHighMulti多生产者多消费者模式;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        // 1、创建RingBuffer
        RingBuffer<Order> ringBuffer = 
                RingBuffer.create(ProducerType.MULTI,
                        new EventFactory<Order>() {
                            @Override
                            public Order newInstance() {
                                return new Order();
                            }
                        },
                        1024 * 1024,
                        new YieldingWaitStrategy());
        // 2、通过ringBuffer， 创建一个屏障 信号屏障
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        
        // 3、创建多个消费者
        Consumer[] consumers = new Consumer[10];
        for (int i = 0; i < consumers.length; i++) {
            consumers[i] = new Consumer("C" + i);
        }
        
        // 4、构建多消费者工作池
        WorkerPool<Order> workerPool = new WorkerPool<>(
                ringBuffer,
                sequenceBarrier,
                new EventExceptionHandler(),
                consumers
        );
        
        // 5、设置多个消费者的Sequence序号 用于 单独统计消费进度， 并且设置到RingBuffer中
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());// 添加消费者序号
        
        // 6、启动workerPool
        workerPool.start(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
        
        // 
        CountDownLatch latch = new CountDownLatch(1);

        CountDownLatch latch1 = new CountDownLatch(10000);

        for (int i = 0; i < 100; i++) {
             Producer producer = new Producer(ringBuffer);
             new Thread(() -> {
                 try{
                     latch.await();
                 }catch (Exception e){
                     e.printStackTrace();
                 }
                 for (int j = 0; j < 100; j++) {
                     producer.sendData(UUID.randomUUID().toString());
                 }
                 
             }).start();
        }
        
        Thread.sleep(2000);

        System.out.println("---------------------线程创建完毕，开始生产数据---------------------");
        latch.countDown();
        
        Thread.sleep(10000);

        System.out.println("任务总数 ：" + consumers[2].getCount());
        
    }
    
    static class EventExceptionHandler implements ExceptionHandler<Order> {
        @Override
        public void handleEventException(Throwable ex, long sequence, Order event) {
            
        }

        @Override
        public void handleOnStartException(Throwable ex) {

        }

        @Override
        public void handleOnShutdownException(Throwable ex) {

        }
    }
}
