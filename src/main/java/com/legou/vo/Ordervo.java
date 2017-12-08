package com.legou.vo;

import com.legou.pojo.Shipping;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/11/20.
 */
public class Ordervo {

    private Long orderNo;

    private BigDecimal payment;

    private Integer paymentType;

    private String paymentTypedesc;

    private Integer postage;

    private Integer status;

    private String statusdesc;

    private String paymentTime;

    private String sendTime;

    private String endTime;

    private String closeTime;

    private String createTime;

    //订单明细

    private List<OrderItemvo> orderItemvoList;

    private String imagehost;
    private Integer shippingid;
    private String receviername;
    private Shippingvo shippingvo;

    public Long getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Long orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public Integer getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(Integer paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentTypedesc() {
        return paymentTypedesc;
    }

    public void setPaymentTypedesc(String paymentTypedesc) {
        this.paymentTypedesc = paymentTypedesc;
    }

    public Integer getPostage() {
        return postage;
    }

    public void setPostage(Integer postage) {
        this.postage = postage;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusdesc() {
        return statusdesc;
    }

    public void setStatusdesc(String statusdesc) {
        this.statusdesc = statusdesc;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<OrderItemvo> getOrderItemvoList() {
        return orderItemvoList;
    }

    public void setOrderItemvoList(List<OrderItemvo> orderItemvoList) {
        this.orderItemvoList = orderItemvoList;
    }

    public String getImagehost() {
        return imagehost;
    }

    public void setImagehost(String imagehost) {
        this.imagehost = imagehost;
    }

    public Integer getShippingid() {
        return shippingid;
    }

    public void setShippingid(Integer shippingid) {
        this.shippingid = shippingid;
    }

    public String getReceviername() {
        return receviername;
    }

    public void setReceviername(String receviername) {
        this.receviername = receviername;
    }

    public Shippingvo getShippingvo() {
        return shippingvo;
    }

    public void setShippingvo(Shippingvo shippingvo) {
        this.shippingvo = shippingvo;
    }
}
