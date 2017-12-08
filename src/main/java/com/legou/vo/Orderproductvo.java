package com.legou.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */
public class Orderproductvo {
    private List<OrderItemvo> orderItemvoList;
    private BigDecimal TotalPrice;

    public List<OrderItemvo> getOrderItemvoList() {
        return orderItemvoList;
    }

    public void setOrderItemvoList(List<OrderItemvo> orderItemvoList) {
        this.orderItemvoList = orderItemvoList;
    }

    public BigDecimal getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }

    private String imageHost;

}
