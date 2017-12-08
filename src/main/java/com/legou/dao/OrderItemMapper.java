package com.legou.dao;

import com.legou.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectbyuseridandorderno(@Param("userid") Integer userid, @Param("orderno") Long orderno);

    List<OrderItem> selectbyorderno( @Param("orderno") Long orderno);

    int Batchinsert(@Param("orderItemList") List<OrderItem> orderItemList);
}