package com.example.demo.DisruptorHigh高准操作;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        // 构建一个线程池用于提交任务
        ExecutorService es = Executors.newFixedThreadPool(4);
        
        // 设置到Disruptor中的线程池参数 
        /**
         * 现在是模拟单消费者模式，有 5 个监听的Handler， 所以这里线程池中最少也得是5个线程
         * 避免这个问题的方法就是使用多消费者模式， 每个消费者单独监听一个
         */
        ExecutorService es1 = Executors.newFixedThreadPool(5);


        // 1、构建Disruptor
        Disruptor<Trade> disruptor = new Disruptor<>(
                new EventFactory<Trade>() {
                    @Override
                    public Trade newInstance() {
                        return new Trade();
                    }
                },
                1024 * 1024,
                es1,
                ProducerType.SINGLE,
                new BusySpinWaitStrategy()
        );

        // 2、把消费者设置到Disruptor中 handleEventWith

        // 2.1、串行操作
        /**
        disruptor.handleEventsWith(new Handler1())
                .handleEventsWith(new Handler2())
                .handleEventsWith(new Handler3());
         */

        // 2.2、并行操作  可以有俩种方式去进行
        // ①、handleEventWith方法 添加多个handle实现即可
        // ②、handleEventWith方法分别进行调用
        /**
        disruptor.handleEventsWith(new Handler1(), new Handler2(), new Handler3());
        disruptor.handleEventsWith(new Handler2());
        disruptor.handleEventsWith(new Handler3());
        */
         
        // 2.3、菱形操作 (一)
        /**
        disruptor.handleEventsWith(new Handler1(), new Handler2())
                .handleEventsWith(new Handler3());
        */
         
        // 2.3、菱形操作 (二)
        /**
        EventHandlerGroup<Trade> ehGroup = disruptor.handleEventsWith(new Handler1(), new Handler2());
        ehGroup.then(new Handler3());
        */
        
        // 2.4、六边形操作
        Handler1 h1 = new Handler1();
        Handler2 h2 = new Handler2();
        Handler3 h3 = new Handler3();
        Handler4 h4 = new Handler4();
        Handler5 h5 = new Handler5();
        /**
         *          并行      并行
         *   串     h1  ----> h2  
         *                          ----> h3
         *   行     h4  ----> h5     
         *      
         */
        disruptor.handleEventsWith(h1, h4);
        disruptor.after(h1).handleEventsWith(h2);
        disruptor.after(h4).handleEventsWith(h5);
        disruptor.after(h2, h5).handleEventsWith(h3);
         
        // 3、启动Disruptor
        RingBuffer<Trade> ringBuffer = disruptor.start();

        CountDownLatch latch = new CountDownLatch(1);

        long begin = System.currentTimeMillis();

        es.submit(new TradePublisher(latch, disruptor));

        latch.await(); // 进行向下

        disruptor.shutdown();
        es.shutdown();
        es1.shutdown();

        System.out.println("总耗时 ：" + (System.currentTimeMillis() - begin) + "ms");

    }

}
