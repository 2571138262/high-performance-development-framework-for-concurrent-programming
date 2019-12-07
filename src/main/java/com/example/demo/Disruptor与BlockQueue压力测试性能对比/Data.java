package com.example.demo.Disruptor与BlockQueue压力测试性能对比;

public class Data {
    
    private Long id;
    private String name;

    public Data() {
    }

    public Data(Long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
