package com.example.demo.DisruptorHighMulti多生产者多消费者模式;

import lombok.Data;

@Data
public class Order {

    private String id;
    private String name;
    private Double price;

}
