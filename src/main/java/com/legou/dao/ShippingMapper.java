package com.legou.dao;

import com.legou.pojo.Shipping;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deletebyidanduserid(@Param("userid") Integer userid,@Param("shippingid") Integer shippingid);

    int updatebyshipping(Shipping record);

    Shipping selectbyshippingidanduserid(@Param("userid") Integer userid,@Param("shippingid") Integer shippingid);

    List<Shipping> selectbyuserid(Integer userid);
}