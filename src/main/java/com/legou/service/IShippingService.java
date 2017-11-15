package com.legou.service;

import com.github.pagehelper.PageInfo;
import com.legou.common.ServiceResponse;
import com.legou.pojo.Shipping;

/**
 * Created by Administrator on 2017/11/11.
 */
public interface IShippingService {
    ServiceResponse add(Integer userid, Shipping shipping);

    ServiceResponse delete(Integer userid,Integer id);

    ServiceResponse update(Integer userid,Shipping shipping);

    ServiceResponse<Shipping> select(Integer userid,Integer shippingid);

    ServiceResponse<PageInfo> list(Integer userid, int pagesize, int pagenum);
}
