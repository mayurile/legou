package com.legou.service.Impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.legou.common.Const;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.dao.*;
import com.legou.pojo.*;
import com.legou.service.IOrderService;
import com.legou.util.BigDecimalUtil;
import com.legou.util.DatetimeUtil;
import com.legou.util.FTPUtil;
import com.legou.util.PropertiesUtil;
import com.legou.vo.OrderItemvo;
import com.legou.vo.Orderproductvo;
import com.legou.vo.Ordervo;
import com.legou.vo.Shippingvo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;


/**
 * Created by Administrator on 2017/11/13.
 */
@Service("iOrderService")
public class IOrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CartMapper cartMapper;


    private static final Logger log= LoggerFactory.getLogger(IOrderServiceImpl.class);


    public ServiceResponse cancelorder(Integer userid,Long Orderno){
        if(userid==null||Orderno==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        Order order=orderMapper.selectbyuseridandordernum(userid,Orderno);
        if(order==null){
            return ServiceResponse.createbyerror("订单不存在");
        }
        if(order.getStatus() !=Const.Orderstatusenum.NOPAY.getCode()){
            return ServiceResponse.createbyerror("已付款，无法取消订单");
        }
        Order updateorder=new Order();
        updateorder.setId(order.getId());
        updateorder.setStatus(Const.Orderstatusenum.CANCEL.getCode());

        int rowcount=orderMapper.updateByPrimaryKeySelective(updateorder);
        if(rowcount>0){
            return ServiceResponse.createbysuccessmsg("取消订单成功");
        }
        return ServiceResponse.createbyerror("取消订单失败");
    }
   public ServiceResponse<PageInfo> orderlist(Integer userid, int pagenum, int pagesize){
       PageHelper.startPage(pagenum,pagesize);
       List<Order> listorder=orderMapper.selectbyuserid(userid);
       List<Ordervo> ordervoList=assembleordervo(listorder,userid);
       PageInfo pageInfo=new PageInfo(listorder);
       pageInfo.setList(ordervoList);
       return ServiceResponse.createbysuccess(pageInfo);
   }

   private List<Ordervo> assembleordervo(List<Order> listorder,Integer userid){
       List<Ordervo> ordervoList=Lists.newArrayList();
       for(Order order:listorder){
           List<OrderItem> orderItemList=Lists.newArrayList();
           if(userid == null){
               //todo 管理员模块
               orderItemList=orderItemMapper.selectbyorderno(order.getOrderNo());
           }else{
               orderItemList = orderItemMapper.selectbyuseridandorderno(userid,order.getOrderNo());
           }
           Ordervo ordervo=assembleordervo(order,orderItemList);
           ordervoList.add(ordervo);
       }
       return  ordervoList;
   }
    public ServiceResponse<Ordervo> getOrderdetail(Integer userid,Long orderno){
        Order order =orderMapper.selectbyuseridandordernum(userid,orderno);
        if(order==null){
            return  ServiceResponse.createbyerror("没有找到该订单");
        }else {
            List<OrderItem> orderItemList=orderItemMapper.selectbyuseridandorderno(userid,orderno);
            Ordervo ordervo=assembleordervo(order,orderItemList);
            return ServiceResponse.createbysuccess(ordervo);
        }
    }

    public ServiceResponse getordercheckproduct(Integer userid){
        Orderproductvo orderproductvo=new Orderproductvo();
        //从购物车获取数据
        List<Cart> cartList=cartMapper.selectbycheckanduserid(userid);
        ServiceResponse serviceResponse=this.selectorderitemfromcart(userid,cartList);
        if(serviceResponse.issuccess()){
            return serviceResponse;
        }
        List<OrderItem> orderItemList=( List<OrderItem>)serviceResponse.getData();

        List<OrderItemvo> orderItemvoList=Lists.newArrayList();

        BigDecimal payment=new BigDecimal("0");
        for(OrderItem orderItem: orderItemList){
            payment=BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemvoList.add(assembleorderitemvo(orderItem));
        }
        orderproductvo.setTotalPrice(payment);
        orderproductvo.setOrderItemvoList(orderItemvoList);
        orderproductvo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return ServiceResponse.createbysuccess(orderproductvo);
    }



    public ServiceResponse pay(Long orderid,Integer userid,String path){
        //返回一个二维码的url和订单号
        Map<String,String> resultmap=Maps.newHashMap();
        Order order=orderMapper.selectbyuseridandordernum(userid,orderid);
        if(order==null){
            return ServiceResponse.createbyerror("订单不存在");
        }
        resultmap.put("orderno",String.valueOf(order.getOrderNo()));


        //生成支付宝订单
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("legou扫码支付,订单号为：").append(outTradeNo.toString()).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
//        String body = "购买商品3件共20.00元";
        String body=new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");



        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";



        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        List<OrderItem> orderItemList=orderItemMapper.selectbyuseridandorderno(userid,order.getOrderNo());
        for(OrderItem orderItem:orderItemList) {
            GoodsDetail goods=GoodsDetail.newInstance(orderItem.getProductId().toString(), orderItem.getProductName(),
                    BigDecimalUtil.multiply(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //todo 注册域名后改这个域名
                .setNotifyUrl("alipay.callback.url")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);


        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();
        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder=new File(path);
                if(!folder.exists()){
                    //路径不存在就创建他
                    folder.setWritable(true);
                    folder.mkdir();
                }
                // 需要修改为运行机器上的路径
                //将二维码的url传到ftp服务器上
                String qrPath = String.format(path+"/qr-%s.png",
                        response.getOutTradeNo());
                String qrfilename=String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);
                File target=new File(path,qrfilename);
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(target));
                } catch (IOException e) {
                    log.error("上传二维码失败",e);
                }
                log.info("qrPath"+qrPath);
                String qrurl= PropertiesUtil.getProperty("ftp.server.http.prefix")+target.getName();
                resultmap.put("qrurl",qrurl);
                return ServiceResponse.createbysuccess(resultmap);
            case FAILED:
                log.error("支付宝预下单失败!!!");
                return ServiceResponse.createbyerror("支付宝预下单失败!!!");

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                return ServiceResponse.createbyerror("系统异常，预下单状态未知!!!");

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                return ServiceResponse.createbyerror("不支持的交易状态，交易返回异常!!!");
        }
    }

    public ServiceResponse alipaycallback(Map<String,String> params){
        Long orderno=Long.parseLong(params.get("out_trade_no"));
        String tradeno=params.get("trade_no");
        String tradestatus=params.get("trade_status");
        Order order=orderMapper.selectbyorderno(orderno);
        if(order==null){
            return ServiceResponse.createbyerror("不合法订单号，支付宝回调失败");
        }
        if(order.getStatus() >= Const.Orderstatusenum.UNDERPAY.getCode()){
            return ServiceResponse.createbysuccessmsg("支付宝重复调用");
        }
        if(Const.AlipayCallback.TRADE_STATUS_TRADE_SUCCESS.equals(tradestatus)){
            //判断回调回来的状态是否是已付款，如果是已付款，将order中的对象状态更新为已支付
            order.setPaymentTime(DatetimeUtil.strtodate(params.get("gmt_payment")));
            order.setStatus(Const.Orderstatusenum.UNDERPAY.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }
        //构建payinfo
        PayInfo payInfo=new PayInfo();
        payInfo.setUserId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.Payplatformenum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeno);
        payInfo.setPlatformStatus(tradestatus);

        payInfoMapper.insert(payInfo);

        return ServiceResponse.createbysuccess();


    }
    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            log.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                log.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            log.info("body:" + response.getBody());
        }
    }

    public  ServiceResponse querypaystatus(Integer userid,Long orderno){
        Order order=orderMapper.selectbyuseridandordernum(userid,orderno);
        if(order==null){
            return ServiceResponse.createbyerror("订单不存在");
        }
        if(order.getStatus() >= Const.Orderstatusenum.UNDERPAY.getCode()){
            return ServiceResponse.createbysuccessmsg("订单付款成功");
        }
        return ServiceResponse.createbyerror();
    }

    public  ServiceResponse createorder(Integer userid,Integer shippingid){
        // 从购物车中获取被勾选的商品信息（sql里创建方法）
        List<Cart> cartList=cartMapper.selectbycheckanduserid(userid);

        // 计算该订单的总价（毕业设计有同样的功能需求）
        ServiceResponse serviceResponse=this.selectorderitemfromcart(userid,cartList);
        if(!serviceResponse.issuccess()){
            return serviceResponse;
        }
        List<OrderItem> orderItemList=(List<OrderItem>)serviceResponse.getData();
        BigDecimal payment=this.createpayment(orderItemList);
        //生成订单
        Order order=this.assembleOrder(userid,shippingid,payment);
        if(order==null){
            return ServiceResponse.createbyerror("生成订单失败");
        }
        if(CollectionUtils.isEmpty(orderItemList)){
            return ServiceResponse.createbyerror("购物车为空");
        }
        for(OrderItem orderItem:orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis批量插入
        orderItemMapper.Batchinsert(orderItemList);
        //减少库存
        this.reducequanlity(orderItemList);
        //清空购物车
        this.cartclean(cartList);
        //返回订单明细（ordervo）
        Ordervo ordervo=assembleordervo(order,orderItemList);
        return ServiceResponse.createbysuccess(ordervo);
    }


    private Ordervo assembleordervo(Order order,List<OrderItem> orderItemList){
        Ordervo ordervo=new Ordervo();
        ordervo.setOrderNo(order.getOrderNo());
        ordervo.setPayment(order.getPayment());
        ordervo.setPaymentType(order.getPaymentType());
        ordervo.setPaymentTypedesc(Const.paymenttypeenum.codeof(order.getPaymentType()).getValue());

        ordervo.setPostage(order.getPostage());
        ordervo.setStatus(order.getStatus());
        ordervo.setStatusdesc(Const.Orderstatusenum.codeof(order.getStatus()).getValue());

        ordervo.setShippingid(order.getShippingId());
        Shipping shipping=shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping!=null){
            ordervo.setReceviername(shipping.getReceiverName());
            ordervo.setShippingvo(this.assembleshippingvo(shipping));
        }
        ordervo.setSendTime(DatetimeUtil.datetostr(order.getSendTime()));
        ordervo.setCloseTime(DatetimeUtil.datetostr(order.getCloseTime()));
        ordervo.setCreateTime(DatetimeUtil.datetostr(order.getCreateTime()));
        ordervo.setEndTime(DatetimeUtil.datetostr(order.getEndTime()));
        ordervo.setPaymentTime(DatetimeUtil.datetostr(order.getPaymentTime()));

        ordervo.setImagehost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemvo> orderItemvolist=Lists.newArrayList();
        for(OrderItem orderItem:orderItemList){
            OrderItemvo orderItemvo=this.assembleorderitemvo(orderItem);
            orderItemvolist.add(orderItemvo);
        }
        ordervo.setOrderItemvoList(orderItemvolist);
        return  ordervo;
    }
    private OrderItemvo assembleorderitemvo(OrderItem orderItem){
        OrderItemvo orderItemvo=new OrderItemvo();

        orderItemvo.setOrderNo(orderItem.getOrderNo());
        orderItemvo.setCreateTime(DatetimeUtil.datetostr(orderItem.getCreateTime()));
        orderItemvo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemvo.setProductId(orderItem.getProductId());
        orderItemvo.setProductImage(orderItem.getProductImage());
        orderItemvo.setProductName(orderItem.getProductName());
        orderItemvo.setQuantity(orderItem.getQuantity());
        orderItemvo.setTotalPrice(orderItem.getTotalPrice());

        return orderItemvo;
    }

    private Shippingvo assembleshippingvo(Shipping shipping){
        Shippingvo shippingvo=new Shippingvo();
        shippingvo.setReceiverAddress(shipping.getReceiverAddress());
        shippingvo.setReceiverCity(shipping.getReceiverCity());
        shippingvo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingvo.setReceiverMobile(shipping.getReceiverMobile());
        shippingvo.setReceiverName(shipping.getReceiverName());
        shippingvo.setReceiverPhone(shipping.getReceiverPhone());
        shippingvo.setReceiverProvince(shipping.getReceiverProvince());
        shippingvo.setReceiverZip(shipping.getReceiverZip());
        return  shippingvo;
    }
    private void cartclean( List<Cart> cartList){
        for(Cart cart:cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    private void reducequanlity(List<OrderItem> orderItemList){
        for(OrderItem orderItem:orderItemList){
            Product product=productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKey(product);
        }
    }

    private Order assembleOrder(Integer userid,Integer shipping,BigDecimal payment){
        Order order=new Order();
        Long orderno=this.generateorderno();
        order.setOrderNo(orderno);
        order.setStatus(Const.Orderstatusenum.NOPAY.getCode());
        order.setPaymentType(Const.paymenttypeenum.PAY_ONLINE.getCode());
        //邮费
        order.setPostage(0);
        order.setPayment(payment);
        order.setUserId(userid);
        order.setShippingId(shipping);
        //发货时间
        //付款时间

        int rowcount=orderMapper.insert(order);
        if(rowcount>0){
            return  order;
        }
        return null;


    }

    private long generateorderno(){
        long currenttime=System.currentTimeMillis();
        //高并发编程，若多人同时下单会有人下单失败
        return currenttime+new Random().nextInt(100);
    }

    private BigDecimal createpayment(List<OrderItem> orderItemList){
        BigDecimal payment=new BigDecimal("0");
        for(OrderItem orderItem : orderItemList){
       payment= BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    private ServiceResponse<List<OrderItem>> selectorderitemfromcart(Integer userid, List<Cart> cartList) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (cartList == null) {
            return ServiceResponse.createbyerror("购物车为空");
        }
        for (Cart cart : cartList) {

        OrderItem orderItem=new OrderItem();
            //获取到商品对象
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            //校验商品状态
            if(Const.productstatusenum.ONSALE.getCode() != product.getStatus()){
                return ServiceResponse.createbyerror("商品"+product.getName()+"不在销售状态");
            }
            //校验商品库存
            if(product.getStock()<cart.getQuantity()){
                return ServiceResponse.createbyerror("商品"+product.getName()+"库存不足");
            }
            //组装订单明细
            orderItem.setUserId(userid);
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setProductId(product.getId());
            orderItem.setTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(),cart.getQuantity().doubleValue()));
            orderItem.setProductName(product.getName());
            orderItemList.add(orderItem);
        }
        return ServiceResponse.createbysuccess(orderItemList);
    }

    //backend
    public ServiceResponse<PageInfo> managelist(int pagenum,int pagesize){
        PageHelper.startPage(pagenum,pagesize);
        //获取所有的订单列表
        List<Order> orderList=orderMapper.selectall();
        List<Ordervo> ordervoList=this.assembleordervo(orderList,null);
        PageInfo pageresult= new PageInfo(orderList);
        pageresult.setList(ordervoList);
        return ServiceResponse.createbysuccess(pageresult);
    }

    public ServiceResponse<Ordervo> managedetail(Long orderno){
        Order order= orderMapper.selectbyorderno(orderno);
        if(order!=null){
            List<OrderItem> orderItemList=orderItemMapper.selectbyorderno(orderno);
            Ordervo ordervo=assembleordervo(order,orderItemList);
            return ServiceResponse.createbysuccess(ordervo);
        }
        return  ServiceResponse.createbyerror("订单不存在");
    }
    public ServiceResponse<PageInfo> managesearch(Long orderno,int pagenum,int pagesize){
        PageHelper.startPage(pagenum,pagesize);
        Order order= orderMapper.selectbyorderno(orderno);
        if(order!=null){
            List<OrderItem> orderItemList=orderItemMapper.selectbyorderno(orderno);
            Ordervo ordervo=assembleordervo(order,orderItemList);
            PageInfo pageresult = new PageInfo(Lists.newArrayList(order));
            pageresult.setList(Lists.newArrayList(ordervo));
            return ServiceResponse.createbysuccess(pageresult);

        }
        return  ServiceResponse.createbyerror("订单不存在");
    }
    public ServiceResponse<String> managesendgoods(Long orderno){
        Order order= orderMapper.selectbyorderno(orderno);
        if(order!=null){
            if(order.getStatus()==Const.Orderstatusenum.UNDERPAY.getCode()){
                order.setStatus(Const.Orderstatusenum.SHIPPID.getCode());
                order.setSendTime(new Date());
                orderMapper.updateByPrimaryKeySelective(order);
                return ServiceResponse.createbysuccess("发货成功");
            }
        }
        return  ServiceResponse.createbyerror("订单不存在");

    }

}

