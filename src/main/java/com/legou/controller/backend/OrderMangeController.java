package com.legou.controller.backend;

import com.github.pagehelper.PageInfo;
import com.legou.common.Const;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.pojo.User;
import com.legou.service.IOrderService;
import com.legou.service.IUserService;
import com.legou.vo.Ordervo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/11/20.
 */
@Controller
@RequestMapping(value = "/manage/order/")
public class OrderMangeController {
    @Autowired
    private IOrderService iOrderService;
    @Autowired
    private IUserService iUserService;
    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse ordermanagelist(HttpSession session, @RequestParam(value = "pagenum",defaultValue = "1") int pagenum,
                                     @RequestParam(value = "pagesize",defaultValue = "10")int pagesize){
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
            //业务逻辑
            return iOrderService.managelist(pagenum,pagesize);
        }
    }
    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse<Ordervo> ordermanagelist(HttpSession session, Long orderno){
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
            //业务逻辑
            return iOrderService.managedetail(orderno);
        }
    }
    @RequestMapping("search.do")
    @ResponseBody
    public ServiceResponse<PageInfo> ordersearch(HttpSession session, Long orderno, @RequestParam(value = "pagenum",defaultValue = "1") int pagenum,
                                                 @RequestParam(value = "pagesize",defaultValue = "10")int pagesize){
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
            //业务逻辑
            return iOrderService.managesearch(orderno,pagenum,pagesize);
        }
    }
    @RequestMapping("sends.do")
    @ResponseBody
    public ServiceResponse<String> Ordersendsgood(HttpSession session, Long orderno){
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
            //业务逻辑
            return iOrderService.managesendgoods(orderno);
        }
    }

}
