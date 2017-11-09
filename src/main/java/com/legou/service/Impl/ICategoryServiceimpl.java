package com.legou.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.legou.common.ServiceResponse;
import com.legou.dao.CategoryMapper;
import com.legou.pojo.Category;
import com.legou.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;


/**
 * Created by Administrator on 2017/11/3.
 */
@Service("iCategoryService")
public class ICategoryServiceimpl implements ICategoryService {
    private Logger logger = LoggerFactory.getLogger(ICategoryServiceimpl.class);
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ServiceResponse addcategory(String categoryname, Integer parantid) {
        if (parantid == null && StringUtils.isNotBlank(categoryname)) {
            return ServiceResponse.createbyerror("添加参数错误");
        }
        Category category = new Category();
        category.setName(categoryname);
        category.setParentId(parantid);
        category.setStatus(true);

        int rowcount = categoryMapper.insert(category);
        if (rowcount > 0) {
            return ServiceResponse.createbysuccessmsg("添加品类成功");
        }
        return ServiceResponse.createbyerror("添加品类失败");
    }

    @Override
    public ServiceResponse updatecategoryname(String categoryname, Integer categoryid) {
        if (categoryid == null && StringUtils.isNotBlank(categoryname)) {
            return ServiceResponse.createbyerror("添加参数错误");
        }
        Category category = new Category();
        category.setId(categoryid);
        category.setName(categoryname);

        int resultcount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultcount > 0) {
            return ServiceResponse.createbysuccessmsg("更新品类名称成功");
        }
        return ServiceResponse.createbyerror("更新品类名称失败");
    }

    public ServiceResponse<List<Category>> getChildrenparallecategory(Integer categoryid) {
        List<Category> list = categoryMapper.selectparentbyid(categoryid);
        if (CollectionUtils.isEmpty(list)) {
            logger.info("未找到子分类");
        }
        return ServiceResponse.createbysuccess(list);
    }

    /**
     * 递归查询本节点和子节点的分类信息
     * @param categoryid
     * @return
     */
    public ServiceResponse<List<Integer>> selectchildrencategorybyid(Integer categoryid) {
        Set<Category> categorySet= Sets.newHashSet();
        findchildren(categorySet,categoryid);
        List<Integer> categoryList= Lists.newArrayList();
        if(categoryid!=null){
            for (Category categoryitem:categorySet){
                categoryList.add(categoryitem.getId());
            }
        }
        return ServiceResponse.createbysuccess(categoryList);

    }

    private Set<Category> findchildren(Set<Category> setc,Integer categoryid){
        Category category=categoryMapper.selectByPrimaryKey(categoryid);
        if(category==null){
            setc.add(category);
        }
        List<Category> categoryList=categoryMapper.selectparentbyid(categoryid);
        for(Category category1:categoryList){
            findchildren(setc,category.getId());
        }
        return setc;
    }
}