package com.legou.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Created by Administrator on 2017/11/1.
 */
public class Const {
    public static  final String CURRENT_USER="currentuser";
    public static final String EMAIL="email";
    public static final String USERNAME="username";
    public static interface ProductListOrderby{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_desc","price_asc");
    }
    public interface Role{
        int ROLECOSTOMMER = 0;//普通用户
        int ROLEMANGER =1; //管理员
    }
    public interface Cart{
        int CHECK =0;//被选中
        int UNCHECK=1;//未被选中

        String LIMIT_NUM_FAIL="LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS="LIMIT_NUM_SUCCESS";
    }
    public enum productstatusenum{
        ONSALE(1,"在线");
        ;
        private String value;
        private int code;
        productstatusenum(int code,String value){
            this.code=code;
            this.value=value;
        }
        public String getValue() {
            return value;
        }
        public int getCode() {
            return code;
        }
    }
    public enum Orderstatusenum{
        CANCEL(0,"已取消"),
        NOPAY(10,"未付款"),
        UNDERPAY(20,"已付款"),
        SHIPPID(30,"已发货"),
        ORDER_SUCCESS(40,"订单完成"),
        ORDER_CLOSED(50,"订单关闭")
        ;
        private String value;
        private int code;

        Orderstatusenum(int code,String value){
            this.code=code;
            this.value=value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static Orderstatusenum codeof(int code){
            for(Orderstatusenum orderstatusenum:values()){
                if(orderstatusenum.getCode()==code){
                    return  orderstatusenum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
    public enum Payplatformenum{
        ALIPAY(1,"支付宝")
        ;

        private String value;
        private int code;

        Payplatformenum(int code,String value){
            this.code=code;
            this.value=value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

    }
    public enum paymenttypeenum{
        PAY_ONLINE(1,"在线支付")
        ;

        private String value;
        private int code;

        paymenttypeenum(int code,String value){
            this.code=code;
            this.value=value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }

        public static paymenttypeenum codeof(int code){
            for(paymenttypeenum paymenttypeenum1:values()){
                if(paymenttypeenum1.getCode()==code){
                    return  paymenttypeenum1;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }

    }
    public interface AlipayCallback{
        String TRADE_STATUS_WAIT_BUYER_PAY ="WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS ="TRADE_SUCCESS";

        String RESPONSE_SUCCESS="success";
        String RESPONSE_FAILED = "failed";
    }
}
