package com.legou.controller.portal;

import com.github.pagehelper.PageInfo;
import com.legou.common.ServiceResponse;
import com.legou.service.IProductService;
import com.legou.vo.ProductdetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/11/7.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;
    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse<ProductdetailVo> productdetail(Integer productid){
        return iProductService.getproductvo(productid);
    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse<PageInfo> productlist(@RequestParam(value = "categoryid",required = false)Integer categoryid,
                                                 @RequestParam(value = "keyword",required = false) String keyword,
                                                 @RequestParam(value = "pagenum",defaultValue = "1") int pagenum,
                                                 @RequestParam(value = "pagesize",defaultValue = "10")int pagesize,
                                                 @RequestParam(value = "orderby",defaultValue = "")String orderby){
        return iProductService.getproductbycategoryid(keyword,categoryid,pagenum,pagesize,orderby);

    }
}
