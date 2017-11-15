package com.legou.dao;

import com.legou.pojo.Order;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectbyuseridandordernum(@Param("userid") Integer userid,@Param("orderno") Long orderno);

    Order selectbyorderno(Long orderno);
}