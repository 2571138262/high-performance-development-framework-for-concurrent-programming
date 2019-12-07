package com.example.demo.并发编程回顾;

import java.util.concurrent.locks.LockSupport;

public class LockSupportExample {

    public static void main(String[] args) throws InterruptedException {
        
        Thread A = new Thread(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                for (int i = 0; i < 10; i++) {
                    sum += i;
                }

                try {
                    Thread.sleep(4000);  // 此处睡眠四秒相当于 阻塞和唤醒 的顺序变了， 先唤醒 再阻塞
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // 静态底层类 让当前线程挂起等待
                LockSupport.park();
                System.out.println("sum = " + sum);
            }
        });
        
        A.start();
        
        Thread.sleep(1000);
        
        LockSupport.unpark(A);  // 先与LockSupport.park(); 执行
        
        
    }
    
}
