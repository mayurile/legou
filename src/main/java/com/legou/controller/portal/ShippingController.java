package com.legou.controller.portal;

import com.github.pagehelper.PageInfo;
import com.legou.common.Const;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.pojo.Shipping;
import com.legou.pojo.User;
import com.legou.service.IShippingService;
import com.sun.deploy.nativesandbox.comm.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/11/11.
 */
@Controller
@RequestMapping(value = "/shipping/")
public class ShippingController {
    @Autowired
    private IShippingService iShippingService;
    @RequestMapping("add.do")
    @ResponseBody
    public ServiceResponse addship(HttpSession httpSession, Shipping shipping){
        //springmvc对象数据直接绑定
        //自己写的与教程的一致，有进步！
        User user =(User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
//            return ServiceResponse.createbyerror("用户未登录，请先登录");
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.add(user.getId(),shipping);
    }
    @RequestMapping("delete.do")
    @ResponseBody
    public ServiceResponse deleteship(HttpSession httpSession,Integer shippingid){
        //springmvc对象数据直接绑定
        //自己写的与教程的一致，有进步！
        User user =(User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
//            return ServiceResponse.createbyerror("用户未登录，请先登录");
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.delete(user.getId(),shippingid);
    }
    @RequestMapping("update.do")
    @ResponseBody
    public ServiceResponse updateship(HttpSession httpSession, Shipping shipping){
        //springmvc对象数据直接绑定
        //自己写的与教程的一致，有进步！
        User user =(User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
//            return ServiceResponse.createbyerror("用户未登录，请先登录");
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServiceResponse searchship(HttpSession httpSession,Integer shippingid){
        //springmvc对象数据直接绑定
        //自己写的与教程的一致，有进步！
        User user =(User)httpSession.getAttribute(Const.CURRENT_USER);
        if(user==null){
//            return ServiceResponse.createbyerror("用户未登录，请先登录");
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.select(user.getId(),shippingid);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse<PageInfo> shiplist(@RequestParam(value = "pagenum",defaultValue = "1") int pagenum,
                                              @RequestParam(value = "pagesize",defaultValue = "10")int pagesize,
                                              HttpSession session){
        User user =(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
//            return ServiceResponse.createbyerror("用户未登录，请先登录");
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(), ResponseCode.NEED_LOGIN.getdesc());
        }
        return iShippingService.list(user.getId(),pagesize,pagenum);

    }


}
