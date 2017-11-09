package com.legou.dao;

import com.legou.pojo.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> selectbynameandid(@Param("productname") String productname,@Param("productid") Integer productid);

    List<Product> selectbycategoryidandname(@RequestParam("productname")String productname,@RequestParam("categoryidlist") List<Integer> categoryidlist);
}