package com.example.demo.DisruptorHigh高准操作;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Disruptor 中的 Event —— 数据
 */
@Data
public class Trade {
    
    private String id;
    private String name;
    private Double price;
    private AtomicInteger count = new AtomicInteger(0);
    
}
