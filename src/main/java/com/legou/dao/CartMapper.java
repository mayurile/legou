package com.legou.dao;

import com.legou.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectbyuseridandproductid(@Param("productid") Integer productid,@Param("userid") Integer userid);

    List<Cart> selectbyuserid(Integer userid);

    int selectcartproductcheckstatus(Integer userid);

    int deletebyuseridproductids(@Param("userid")Integer userid,@Param("productids")List<String> productids);
   //更改成全选或全不选
    int checkoruncheckedproduct(@Param("checked") Integer checked,@Param("productid") Integer productid,@Param("userid") Integer userid);

    int selectcartcount(Integer userid);

    List<Cart> selectbycheckanduserid(Integer userid);
}