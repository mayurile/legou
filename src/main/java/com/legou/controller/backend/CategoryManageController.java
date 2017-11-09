package com.legou.controller.backend;

import com.legou.common.Const;
import com.legou.common.ServiceResponse;
import com.legou.pojo.Category;
import com.legou.pojo.User;
import com.legou.service.ICategoryService;
import com.legou.service.IUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/11/3.
 */
@Controller
@RequestMapping(value = "/manage/category/")
public class CategoryManageController {
    @Autowired
    private IUserService iUserService;
    @Autowired
    private ICategoryService iCategoryService;
    @RequestMapping("addcategory.do")
    @ResponseBody
    public ServiceResponse addcategory(HttpSession session, String categoryname,@RequestParam(value = "parentid",defaultValue = "0") int parentid){
        //校验是否登录
        User Currentuser=(User)session.getAttribute(Const.CURRENT_USER);
        if(Currentuser==null){
            return ServiceResponse.createbyerror("用户未登录");
        }
        //校验是否为管理员，管理员才可以进行这个操作
        ServiceResponse response=iUserService.checkadminrole(Currentuser);
        if(!response.issuccess()){
            //非管理员
            return ServiceResponse.createbyerror("无操作权限");
        }
        else{
            //是管理员
            //添加分类
           return iCategoryService.addcategory(categoryname,parentid);
        }


    }

    @RequestMapping("updatecategoryname.do")
    @ResponseBody
    public ServiceResponse updatecategoryname(HttpSession session, String categoryname,Integer categoryid) {
        //校验是否登录
        User Currentuser = (User) session.getAttribute(Const.CURRENT_USER);
        if (Currentuser == null) {
            return ServiceResponse.createbyerror("用户未登录");
        }
        //校验是否为管理员，管理员才可以进行这个操作
        ServiceResponse response = iUserService.checkadminrole(Currentuser);
        if (!response.issuccess()) {
            //非管理员
            return ServiceResponse.createbyerror("无操作权限");
        } else {
            //更新分类的名字
            return iCategoryService.updatecategoryname(categoryname,categoryid);
        }
    }
    @RequestMapping("getChildrencategory.do")
    @ResponseBody
    public ServiceResponse getChildrenparallelcategory(HttpSession session,@RequestParam(value = "categoryid",defaultValue = "0") Integer categoryid){
    //先校验是否登录
        User CurrentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(CurrentUser==null){
            return ServiceResponse.createbyerror("用户未登录");
        }
        if(iUserService.checkadminrole(CurrentUser).issuccess()){
            //查询子类同级的分类结点
            return iCategoryService.getChildrenparallecategory(categoryid);
        }else{
            //非管理员
            return ServiceResponse.createbyerror("无操作权限");
        }
    }

    @RequestMapping("getdeepcategory.do")
    @ResponseBody
    public ServiceResponse getdeepcategory(HttpSession session,@RequestParam(value = "categoryid",defaultValue = "0") Integer categoryid){
        //先校验是否登录
        User CurrentUser=(User)session.getAttribute(Const.CURRENT_USER);
        if(CurrentUser==null){
            return ServiceResponse.createbyerror("用户未登录");
        }
        if(iUserService.checkadminrole(CurrentUser).issuccess()){
            //查询当前节点和递归的子节点
            return iCategoryService.selectchildrencategorybyid(categoryid);
        }else{
            //非管理员
            return ServiceResponse.createbyerror("无操作权限");
        }
    }
}
