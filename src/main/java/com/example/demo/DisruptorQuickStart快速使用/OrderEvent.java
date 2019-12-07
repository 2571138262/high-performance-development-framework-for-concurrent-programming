package com.example.demo.DisruptorQuickStart快速使用;

import lombok.Data;

/**
 * 订单事件
 */
@Data
public class OrderEvent {
    
    private long value; // 订单价格
    
}
