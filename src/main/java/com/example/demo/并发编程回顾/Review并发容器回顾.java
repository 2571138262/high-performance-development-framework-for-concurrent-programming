package com.example.demo.并发编程回顾;

import com.google.common.collect.Maps;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class Review并发容器回顾 {

    public static void main(String[] args) throws InterruptedException {
        
        // ConcurrentHashMap 
//        ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();


        // CopyOnWriteArrayList
//        CopyOnWriteArrayList<String> cowal = new CopyOnWriteArrayList<>();
//        cowal.add("aaaa");
        
        // Atomic系列类 & Unsafe类
//        AtomicLong count = new AtomicLong(1);
//        boolean b = count.compareAndSet(0, 2);
//        System.out.println(b);
//        System.out.println(count.get());
        
        
        /**Object lock = new Object();
        
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                for (int i = 0; i < 10; i++) {
                    sum += i;
                }
                
                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("sum = " + sum);
            }
        });
        
        A.start();
        
        Thread.sleep(3000);
        
        synchronized (lock){
            lock.notify();
        }*/


        // 线程池
        /**
        Executors.newCachedThreadPool();
        Executors.newFixedThreadPool(10);
        
        // Runtime.getRuntime().availableProcessors(), 系统的核数 - 系统的可用线程数
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                5,                                                     // 核心线程数
                Runtime.getRuntime().availableProcessors() * 2,   // 最大线程数量
                60,                                                   // 线程的最大空闲时间
                TimeUnit.SECONDS,                                                    // 空闲时间的单位 
                new ArrayBlockingQueue<>(200),         // 有界队列         // 线程池的阻塞队列  
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        // 线程检查 和 定制化
                        thread.setName("order-thread");
                        // 防止线程被篡改成守护线程
                        if (thread.isDaemon()) {
                            thread.setDaemon(false);
                        }
                        // 防止为线程设置优先级
                        if (Thread.NORM_PRIORITY != thread.getPriority()) {
                            thread.setPriority(Thread.NORM_PRIORITY);
                        }
                        return thread;
                    }
                },
                new RejectedExecutionHandler() {
                    @Override
                    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                        System.out.println("拒绝策略");
                    }
                }
        );*/
            
        
        // AQS 底层架构
        ReentrantLock reentrantLock = new ReentrantLock();
        ReentrantLock reentrantLock1 = new ReentrantLock(true); // 公平锁
        reentrantLock1 = new ReentrantLock(false);              // 非公平锁
        
        
        CountDownLatch count = new CountDownLatch(1);
        
    }
    
}
