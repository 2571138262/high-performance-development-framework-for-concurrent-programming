package com.example.demo.DisruptorQuickStart快速使用;

import com.lmax.disruptor.EventHandler;

/**
 * 具体的消费者， 用于处理数据 (Event类)
 */
public class OrderEventHandler implements EventHandler<OrderEvent> {

    /**
     * 事件监听模式， 当有一个消息事件发布的时候，这里就能监听到
     * @param orderEvent
     * @param l
     * @param b
     * @throws Exception
     */
    @Override
    public void onEvent(OrderEvent orderEvent, long l, boolean b) throws Exception {
        System.out.println("消费者 : " + orderEvent.getValue());
    }
}
