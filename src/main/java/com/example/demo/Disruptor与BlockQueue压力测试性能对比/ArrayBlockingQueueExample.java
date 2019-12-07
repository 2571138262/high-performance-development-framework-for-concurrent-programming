package com.example.demo.Disruptor与BlockQueue压力测试性能对比;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * 使用阻塞队列来进行单线程并发读取数据
 */
public class ArrayBlockingQueueExample {

    public static void main(String[] args) {
        final ArrayBlockingQueue<Data> queue = new ArrayBlockingQueue<Data>(100000000);
        final long startTime = System.currentTimeMillis();
        
        // 向容器中添加元素
        new Thread(() -> {
           long i = 0;
           while (i < Constants.EVENT_NUM_OHM){
               Data data = new Data(i, "c" + i);
               try{
                    queue.put(data);
               }catch (InterruptedException e){
                   e.printStackTrace();
               }
               i ++;
           }
        }).start();

        // 从容器中获取元素
        new Thread(() -> {
            long k = 0;
            while (k < Constants.EVENT_NUM_OHM){
                try{
                    Data take = queue.take();                    
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                k ++;
            }
            long endTime = System.currentTimeMillis();
            System.out.println("ArrayBlockingQueue costTime = " + (endTime - startTime));
        }).start();
    }
    
}
