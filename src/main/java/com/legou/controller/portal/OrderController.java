package com.legou.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.legou.common.Const;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.pojo.User;
import com.legou.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Administrator on 2017/11/13.
 */
@Controller("/order")
public class OrderController {


    private static final Logger logger= LoggerFactory.getLogger(OrderController.class);
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServiceResponse create(HttpSession session,Integer shippingid){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iOrderService.createorder(user.getId(),shippingid);
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public ServiceResponse cancel(HttpSession session,Long orderno){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iOrderService.cancelorder(user.getId(),orderno);
    }
    @RequestMapping("get_cart_product_.do")
    @ResponseBody
    public ServiceResponse getcartproduct(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iOrderService.getordercheckproduct(user.getId());
    }
    @RequestMapping("detail.do")
    @ResponseBody
    public ServiceResponse detail(HttpSession session,Long orderno){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iOrderService.getOrderdetail(user.getId(),orderno);
    }
    @RequestMapping("list.do")
    @ResponseBody
    public ServiceResponse orderlist(HttpSession session,@RequestParam(value = "pagenum",defaultValue = "1") int pagenum,
                                     @RequestParam(value = "pagesize",defaultValue = "10")int pagesize){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        return iOrderService.orderlist(user.getId(),pagenum,pagesize);
    }



























    @RequestMapping("pay.do")
    @ResponseBody
    public ServiceResponse pay(HttpSession session, HttpServletRequest request, Long ordernum){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        String path=request.getServletContext().getRealPath("upload");
        return iOrderService.pay(ordernum,user.getId(),path);
    }
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object reback(HttpServletRequest request) {
        Map<String, String> params = Maps.newHashMap();

        Map requestmap = request.getParameterMap();
        for (Iterator iter = requestmap.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestmap.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        logger.info("支付宝回调,sign:{},trade_status:{},参数:{}", params.get("sign"), params.get("trade_status"), params.toString());
//        return iOrderService.pay(ordernum,user.getId(),path);
        //验证回调的正确性，是不是支付宝发的，还有避免重复通知
        params.remove("sign_type");
        try {
            boolean alipayRSAcheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType());
            if (!alipayRSAcheckedV2) {
                return ServiceResponse.createbyerror("非法请求，不通过,ip已被记录");
            }
            //业务代码
        } catch (AlipayApiException e) {
            logger.error("支付宝回调异常", e);
        }
        //to.do 校验各种数据
        ServiceResponse serviceResponse = iOrderService.alipaycallback(params);
        if (serviceResponse.issuccess()) {
            return Const.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallback.RESPONSE_FAILED;
    }
    @RequestMapping("query_order_pay_status.do")
    @ResponseBody
    public ServiceResponse<Boolean> queryOrderpaystatus(HttpSession session,Long ordernum){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        if(user==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),ResponseCode.NEED_LOGIN.getdesc());
        }
        ServiceResponse serviceResponse=iOrderService.querypaystatus(user.getId(),ordernum);
        if(serviceResponse.issuccess()){
            return ServiceResponse.createbysuccess(true);
        }else {
            return ServiceResponse.createbysuccess(false);
        }
    }



}
