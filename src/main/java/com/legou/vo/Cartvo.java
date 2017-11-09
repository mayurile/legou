package com.legou.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */
public class Cartvo {
    private List<Cartproductvo> cartproductvoList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;
    private String imageHost;

    public List<Cartproductvo> getCartproductvoList() {
        return cartproductvoList;
    }

    public void setCartproductvoList(List<Cartproductvo> cartproductvoList) {
        this.cartproductvoList = cartproductvoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
