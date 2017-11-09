package com.legou.service;

import com.legou.common.ServiceResponse;
import com.legou.vo.Cartvo;

/**
 * Created by Administrator on 2017/11/9.
 */
public interface ICartService {
    //添加购物车列表
    ServiceResponse<Cartvo> add(Integer productid, Integer userid, Integer count);
    //更新购物车内的商品数量
    ServiceResponse<Cartvo> update(Integer productid,Integer userid,Integer count);
    //删除购物车内的指定产品
    ServiceResponse<Cartvo> delete(Integer userid,String productids);
   //显示当前用户的所有购物车信息
    ServiceResponse<Cartvo> list(Integer userid);
    //勾选或勾反选
    ServiceResponse<Cartvo> selectorUn(Integer userid,Integer productid,Integer checked);
    //获取购物车商品的总数
    ServiceResponse<Integer> getcartcount(Integer userid);
}
