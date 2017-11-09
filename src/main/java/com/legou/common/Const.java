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
}
