package com.legou.service.Impl;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.legou.common.Const;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.dao.CategoryMapper;
import com.legou.dao.ProductMapper;
import com.legou.pojo.Category;
import com.legou.pojo.Product;
import com.legou.service.ICategoryService;
import com.legou.service.IProductService;
import com.legou.util.DatetimeUtil;
import com.legou.util.PropertiesUtil;
import com.legou.vo.ProductdetailVo;
import com.legou.vo.ProductlistVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/6.
 */
@Service("iProductService")
public class IProductServiceimpl implements IProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServiceResponse<String> productsaveorupdate(Product product){
        if (product==null){
            return ServiceResponse.createbyerror("参数错误");
        }else {
            if(StringUtils.isNotBlank(product.getSubImages())){
                String[] subimagearray=product.getSubImages().split(",");
                if(subimagearray.length>0){
                    product.setMainImage(subimagearray[0]);
                }
            }
            if (product.getId() == null) {
                int rowcount = productMapper.insert(product);
                if (rowcount > 0) {
                    return ServiceResponse.createbysuccessmsg("添加产品成功");
                } else {
                    return ServiceResponse.createbyerror("添加产品失败");
                }
            } else {
                int resultcount = productMapper.updateByPrimaryKey(product);
                if (resultcount > 0) {
                    return ServiceResponse.createbysuccessmsg("更新产品成功");
                } else {
                    return ServiceResponse.createbyerror("更新产品失败");
                }
            }
        }
    }

    public ServiceResponse<String> updateproductstatus(Integer productid,Integer status){
        if(productid==null || status==null){
            return  ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        Product product=new Product();
        product.setId(productid);
        product.setStatus(status);
        int resultcount=productMapper.updateByPrimaryKeySelective(product);
        if(resultcount>0){
            return ServiceResponse.createbysuccessmsg("产品更新状态成功");
        }else{
            return ServiceResponse.createbyerror("产品更新状态失败");
        }
    }

    public ServiceResponse<ProductdetailVo> manageProductdetail(Integer productid){
        if(productid==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        Product product=productMapper.selectByPrimaryKey(productid);
        if(product==null){
            return ServiceResponse.createbyerror("产品下架或已经删除");
        }
        //返回vo对象--value object
        //pojo->bo->vo
        ProductdetailVo productdetailVo=assembleProductDetailVo(product);
        return  ServiceResponse.createbysuccess(productdetailVo);
    }

    private ProductdetailVo assembleProductDetailVo(Product product){
        ProductdetailVo productdetailVo=new ProductdetailVo();
        productdetailVo.setId(product.getId());
        productdetailVo.setSubtitle(product.getSubtitle());
        productdetailVo.setPrice(product.getPrice());
        productdetailVo.setMainImage(product.getMainImage());
        productdetailVo.setCategoryid(product.getCategoryId());
        productdetailVo.setDetail(product.getDetail());
        productdetailVo.setName(product.getName());
        productdetailVo.setStatus(product.getStatus());
        productdetailVo.setStock(product.getStock());

        //imageHost
        //parentCategoryId
        //createTime
        //updateTime
        //todo 调试好ftp后要改图片默认的host
        productdetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        Category category=categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category==null){
            //设置成默认根节点
            productdetailVo.setParentCategoryId(0);
        }else {
            //使用找到的父节点
            productdetailVo.setParentCategoryId(category.getParentId());
        }
        productdetailVo.setCreateTime(DatetimeUtil.datetostr(product.getCreatTime()));
        productdetailVo.setUpdateTime(DatetimeUtil.datetostr(product.getUpdateTime()));
        return productdetailVo;

    }

    public ServiceResponse<PageInfo> getproductlist(int pagenum,int pagesize){
        //mybatis-pagehelper 使用步骤
        //startpage-start 记录开始
        //填充sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pagenum,pagesize);
        List<Product> productList=productMapper.selectList();
        List<ProductlistVo> productlistVos=new ArrayList<>();
        for(Product product:productList){
            ProductlistVo productlistVo=assembleProductListVo(product);
            productlistVos.add(productlistVo);
        }
        PageInfo pageResult =new PageInfo(productList);
        //把list重置，放入page收尾里
        pageResult.setList(productlistVos);
        return ServiceResponse.createbysuccess(pageResult);
    }

    private ProductlistVo assembleProductListVo(Product product){
        ProductlistVo productlistVo=new ProductlistVo();
        productlistVo.setId(product.getId());
        productlistVo.setMainImage(product.getMainImage());
        productlistVo.setCategoryId(product.getCategoryId());
        productlistVo.setName(product.getName());
        productlistVo.setPrice(product.getPrice());
        productlistVo.setStatus(productlistVo.getStatus());
        productlistVo.setSubtitle(product.getSubtitle());

        //todo 调试好ftp后要改图片默认的host
        productlistVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        return productlistVo;
    }

    public ServiceResponse<PageInfo> productsearch(String name,Integer productid,Integer pagenum,Integer pagesize){
        //记录开始
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotBlank(name)){
            name =new StringBuilder().append("%").append(name).append("%").toString();
        }
        List<Product> productList=productMapper.selectbynameandid(name,productid);
        List<ProductlistVo> productlistVos=new ArrayList<>();
        for(Product product:productList){
            ProductlistVo productlistVo=assembleProductListVo(product);
            productlistVos.add(productlistVo);
        }
        PageInfo pageResult =new PageInfo(productList);
        //把list重置，放入page收尾里
        pageResult.setList(productlistVos);
        return ServiceResponse.createbysuccess(pageResult);
    }

    public ServiceResponse<ProductdetailVo> getproductvo(Integer productid){
        if(productid==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        Product product=productMapper.selectByPrimaryKey(productid);
        if(product==null){
            return ServiceResponse.createbyerror("产品下架或已经删除");
        }
        if(product.getStatus() != Const.productstatusenum.ONSALE.getCode()){
            return ServiceResponse.createbyerror("产品下架或已经删除");
        }
        //返回vo对象--value object
        //pojo->bo->vo
        ProductdetailVo productdetailVo=assembleProductDetailVo(product);
        return  ServiceResponse.createbysuccess(productdetailVo);

    }

    public ServiceResponse<PageInfo> getproductbycategoryid(String keyword,Integer categoryid,int pagenum,int pagesize,String orderby){
        //校验
        if(StringUtils.isBlank(keyword) && categoryid==null) {
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(), ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        List<Integer> categordidlist=new ArrayList<Integer>();
        if(categoryid!=null){
            Category category=categoryMapper.selectByPrimaryKey(categoryid);
            if(category==null && StringUtils.isBlank(keyword)){
                PageHelper.startPage(pagenum,pagesize);
                List<ProductdetailVo> productdetailVos= Lists.newArrayList();
                PageInfo pageInfo=new PageInfo(productdetailVos);
                return ServiceResponse.createbysuccess(pageInfo);
            }
            categordidlist =iCategoryService.selectchildrencategorybyid(category.getId()).getData();
        }

        if(StringUtils.isNotBlank(keyword)){
            //拼接
            keyword=new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pagenum,pagesize);
        if(StringUtils.isNotBlank(orderby)){
            if(Const.ProductListOrderby.PRICE_ASC_DESC.contains(orderby)){
                String[] orderByArray = orderby.split("_");
                PageHelper.orderBy(orderByArray[0]+""+orderByArray[1]);
            }
        }
        List<Product> productList=productMapper.selectbycategoryidandname(StringUtils.isBlank(keyword)?null:keyword,categordidlist.size()==0?null:categordidlist);

        List<ProductlistVo> productdetailVos=Lists.newArrayList();
        for(Product product:productList){
            ProductlistVo productlistVo=assembleProductListVo(product);
            productdetailVos.add(productlistVo);
        }
        PageInfo pageInfo=new PageInfo(productList);
        pageInfo.setList(productdetailVos);
        return ServiceResponse.createbysuccess(pageInfo);
    }



}
