package com.legou.service;

import com.legou.common.ServiceResponse;
import com.legou.pojo.Category;

import javax.sql.rowset.serial.SerialArray;
import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 */
public interface ICategoryService {
     ServiceResponse addcategory(String categoryname,Integer parantid);

     ServiceResponse updatecategoryname(String categoryname,Integer categoryid);

    ServiceResponse<List<Category>> getChildrenparallecategory(Integer categoryid);

    ServiceResponse<List<Integer>> selectchildrencategorybyid(Integer categoryid);

}
