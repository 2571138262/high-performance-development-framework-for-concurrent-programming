package com.example.demo.Disruptor与BlockQueue压力测试性能对比;


import com.lmax.disruptor.EventHandler;

public class DataConsumer implements EventHandler<Data> {
    
    private long startTime;
    private int i;

    public DataConsumer(){
        this.startTime  = System.currentTimeMillis();
    }
    
    @Override
    public void onEvent(Data data, long l, boolean b) throws Exception {
        i ++;
        if (i == Constants.EVENT_NUM_OHM){
            long endTime = System.currentTimeMillis();
            System.out.println("Disruptor costTime = " + (endTime - startTime) + "ms");
        }
    }
}
