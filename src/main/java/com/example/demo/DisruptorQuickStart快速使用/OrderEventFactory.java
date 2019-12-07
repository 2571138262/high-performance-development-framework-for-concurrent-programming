package com.example.demo.DisruptorQuickStart快速使用;

import com.lmax.disruptor.EventFactory;

/**
 * 订单工厂，用于创建订单对象
 */
public class OrderEventFactory implements EventFactory<OrderEvent> {

    @Override
    public OrderEvent newInstance() {
        return new OrderEvent(); // 这个方法就是为了返回空的消息对象 或者说 数据对象 (Event)
    }
}
