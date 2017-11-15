package com.legou.service;

import com.legou.common.ServiceResponse;

import java.util.Map;

/**
 * Created by Administrator on 2017/11/13.
 */
public interface IOrderService {
    ServiceResponse pay(Long orderid, Integer userid, String path);

    ServiceResponse alipaycallback(Map<String,String> params);

    ServiceResponse querypaystatus(Integer userid,Long orderno);
}
