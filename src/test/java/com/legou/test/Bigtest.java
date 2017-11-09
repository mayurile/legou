package com.legou.test;

import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by Administrator on 2017/11/9.
 */
public class Bigtest {

    @Test
    public void test1(){
        System.out.print(0.05+0.01);
        System.out.print(1.0-0.42);
        System.out.print(4.015*100);
        System.out.print(123.3/100);
    }
    @Test
    public void test2(){
        BigDecimal b1=new BigDecimal("0.05");
        BigDecimal b2=new BigDecimal("0.01");
        System.out.print(b1.add(b2));
    }
}
