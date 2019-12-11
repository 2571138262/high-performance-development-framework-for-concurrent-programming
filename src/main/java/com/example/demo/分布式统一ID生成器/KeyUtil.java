package com.example.demo.分布式统一ID生成器;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;

/**
 * ID生成器
 *      单点情况下没有冲突，
 *      有序的ID
 */
public class KeyUtil {
    
    public static String generatorUUID(){
        TimeBasedGenerator timeBasedGenerator = Generators.timeBasedGenerator(EthernetAddress.fromInterface());
        return timeBasedGenerator.generate().toString();
    }


    public static void main(String[] args) {
        System.out.println(KeyUtil.generatorUUID());
        System.out.println(KeyUtil.generatorUUID());
    }
    
}
