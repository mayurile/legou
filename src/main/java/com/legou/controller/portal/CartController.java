package com.legou.controller.portal;

import com.legou.common.Const;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.pojo.User;
import com.legou.service.ICartService;
import com.legou.vo.Cartvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/11/9.
 */
@Controller
@RequestMapping("/Cart/")
public class CartController  {

    @Autowired
    private ICartService iCartService;
    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse<Cartvo> list(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iCartService.list(user.getId());
    }
    @RequestMapping("add.do")
    @ResponseBody
    public ServiceResponse<Cartvo> add(HttpSession session, Integer productId, Integer count){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iCartService.add(productId,user.getId(),count);
    }
    @RequestMapping("update.do")
    @ResponseBody
    public ServiceResponse update(HttpSession session, Integer productId, Integer count){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iCartService.update(productId,user.getId(),count);
    }

    /**
     *删除购物车商品
     * @param session
     * @param productId 以","为分隔的字符串
     * @return
     */
    @RequestMapping("delete.do")
    @ResponseBody
    public ServiceResponse delete(HttpSession session, String productId){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iCartService.delete(user.getId(),productId);
   }//tudo
   //全选
   //全反选
   @RequestMapping("selectall.do")
   @ResponseBody
   public ServiceResponse selectall(HttpSession session){
       User user=(User)session.getAttribute(Const.CURRENT_USER);
       if(user==null){
           return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
       }
       return iCartService.selectorUn(user.getId(),null,Const.Cart.CHECK);
   }
    @RequestMapping("unselectall.do")
    @ResponseBody
    public ServiceResponse unselectall(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iCartService.selectorUn(user.getId(),null,Const.Cart.UNCHECK);
    }

    //单独选
    //单独反选
    @RequestMapping("unselect.do")
    @ResponseBody
    public ServiceResponse unselect(HttpSession session,Integer productid){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iCartService.selectorUn(user.getId(),productid,Const.Cart.UNCHECK);
    }
    @RequestMapping("select.do")
    @ResponseBody
    public ServiceResponse select(HttpSession session,Integer productid){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iCartService.selectorUn(user.getId(),productid,Const.Cart.CHECK);
    }
    //查询购物车内商品的数量

    @RequestMapping("getcartproductcount.do")
    @ResponseBody
    public ServiceResponse<Integer> getcartproductcount(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbysuccess(0);
        }
        return iCartService.getcartcount(user.getId());
    }

}
