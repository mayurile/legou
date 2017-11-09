package com.legou.service;

import com.github.pagehelper.PageInfo;
import com.legou.common.ServiceResponse;
import com.legou.pojo.Product;
import com.legou.vo.ProductdetailVo;

/**
 * Created by Administrator on 2017/11/6.
 */
public interface IProductService {
    ServiceResponse productsaveorupdate(Product product);

    ServiceResponse updateproductstatus(Integer productid,Integer status);

    ServiceResponse<ProductdetailVo> manageProductdetail(Integer productid);

    ServiceResponse<PageInfo> getproductlist(int pagenum, int pagesize);

    ServiceResponse<PageInfo> productsearch(String name,Integer productid,Integer pagenum,Integer pagesize);

    ServiceResponse<ProductdetailVo> getproductvo(Integer productid);

    ServiceResponse<PageInfo> getproductbycategoryid(String keyword,Integer categoryid,int pagenum,int pagesize,String orderby);
}
