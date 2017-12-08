package com.legou.service;

import com.github.pagehelper.PageInfo;
import com.legou.common.ServiceResponse;
import com.legou.vo.Ordervo;

import java.util.Map;
public interface IOrderService {
    ServiceResponse pay(Long orderid, Integer userid, String path);

    ServiceResponse alipaycallback(Map<String,String> params);

    ServiceResponse querypaystatus(Integer userid,Long orderno);

    ServiceResponse createorder(Integer userid,Integer shippingid);

    ServiceResponse cancelorder(Integer userid,Long Orderno);

    ServiceResponse getordercheckproduct(Integer userid);

    ServiceResponse<Ordervo> getOrderdetail(Integer userid, Long orderno);

    ServiceResponse<PageInfo> orderlist(Integer userid, int pagenum, int pagesize);
//backend
    ServiceResponse<PageInfo> managelist(int pagenum,int pagesize);

    ServiceResponse<Ordervo> managedetail(Long orderno);

    ServiceResponse<PageInfo> managesearch(Long orderno,int pagenum,int pagesize);

    ServiceResponse<String> managesendgoods(Long orderno);
}
