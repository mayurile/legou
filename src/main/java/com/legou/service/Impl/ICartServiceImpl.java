package com.legou.service.Impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.legou.common.Const;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.dao.CartMapper;
import com.legou.dao.ProductMapper;
import com.legou.pojo.Cart;
import com.legou.pojo.Product;
import com.legou.service.ICartService;
import com.legou.util.BigDecimalUtil;
import com.legou.util.PropertiesUtil;
import com.legou.vo.Cartproductvo;
import com.legou.vo.Cartvo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Administrator on 2017/11/9.
 */
@Service("iCartService")
public class ICartServiceImpl implements ICartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServiceResponse<Cartvo> add(Integer productid,Integer userid,Integer count){
        //校验参数的有效性
        if(productid==null||count==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        Cart cart=cartMapper.selectbyuseridandproductid(productid,userid);
        if(cart==null){
            //产品不在购物车列表中
            //需要添加仅需
            Cart cartitem=new Cart();
            cartitem.setProductId(productid);
            cartitem.setUserId(userid);
            cartitem.setChecked(Const.Cart.CHECK);
            cartitem.setQuantity(count);
            cartMapper.insert(cartitem);
        }else {
            //产品已经在购物车列表中
            //如果产品已存在，数量叠加
            count =cart.getQuantity()+count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //封装一个购物车方法的核心类getcartlimit
        return this.list(userid);
    }

    public ServiceResponse<Cartvo> update(Integer productid,Integer userid,Integer count){
        //校验参数的有效性
        if(productid==null||count==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        Cart cart=cartMapper.selectbyuseridandproductid(productid,userid);
        if(cart!=null){
            cart.setQuantity(count);
        }
        //更新购物车中商品的数量，然后构造cartvo进行返回
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userid);

    }
    public ServiceResponse<Cartvo> delete(Integer userid,String productids){
        //使用guwa的数组生成，判断已逗号隔开形成数组
        List<String> idlist= Splitter.on(",").splitToList(productids);
        if(CollectionUtils.isEmpty(idlist)){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        cartMapper.deletebyuseridproductids(userid,idlist);
        return this.list(userid);
    }

    public ServiceResponse<Cartvo> list(Integer userid){
        Cartvo cartvo=this.getcartlimit(userid);
        return ServiceResponse.createbysuccess(cartvo);
    }

    public ServiceResponse<Cartvo> selectorUn(Integer userid,Integer productid,Integer checked){
    cartMapper.checkoruncheckedproduct(userid,productid,checked);
    return this.list(userid);
    }

    public ServiceResponse<Integer> getcartcount(Integer userid){
        if(userid==null){
            return ServiceResponse.createbysuccess(0);
        }
        return ServiceResponse.createbysuccess(cartMapper.selectcartcount(userid));
    }








//购物车构造核心方法
    private Cartvo getcartlimit(Integer userid){
        Cartvo cartvo=new Cartvo();
        List<Cart> cartList=cartMapper.selectbyuserid(userid);
        List<Cartproductvo> cartproductvoList= Lists.newArrayList();
        BigDecimal cartTotalPrice=new BigDecimal("0");//避免丢失精度,使用String构造方法（商业计算中一定要用这个）
        //数据库中存的不是bigdecimal。要重写一个BigDecimal的工具类
        //将同个用户下的购物车信息提取出来放入cartproductvo中
        //foreach循环遍历提取构造Cartproductvo
        if(!CollectionUtils.isEmpty(cartList)){
            for(Cart cart:cartList){
                Cartproductvo cartproductvo=new Cartproductvo();
                cartproductvo.setId(cart.getId());
                cartproductvo.setProductid(cart.getProductId());
                cartproductvo.setUserid(cart.getUserId());

                Product product=productMapper.selectByPrimaryKey(cart.getProductId());
                if(product!=null){
                    cartproductvo.setProductname(product.getName());
                    cartproductvo.setProductMainImage(product.getMainImage());
                    cartproductvo.setProductStock(product.getStock());
                    cartproductvo.setProductStatus(product.getStatus());
                    cartproductvo.setProductPrice(product.getPrice());
                    cartproductvo.setProductSubtitle(product.getSubtitle());
                    //判断库存
                    int buylimitcount=0;
                    //当库存大于购物车数量时，认为是成功的
                    if(product.getStock()>=cart.getQuantity()){
                        //库存充足
                        buylimitcount=cart.getQuantity();
                        cartproductvo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else{
                        //库存不足
                        buylimitcount=product.getStock();
                        cartproductvo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        //购物车中设置有效库存
                        Cart cartfquanlity=new Cart();
                        cartfquanlity.setId(cart.getId());
                        cartfquanlity.setQuantity(buylimitcount);
                        cartMapper.updateByPrimaryKeySelective(cartfquanlity);
                    }
                    cartproductvo.setQuantity(buylimitcount);
                    //计算总价
                    cartproductvo.setProductTotalPrice(BigDecimalUtil.multiply(product.getPrice().doubleValue(),cartproductvo.getQuantity()));
                    cartproductvo.setProductChecked(cart.getChecked());
                }
                //计算购物车的总价
                if(cart.getChecked()==Const.Cart.CHECK){
                    cartTotalPrice=BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartproductvo.getProductTotalPrice().doubleValue());
                }
                cartproductvoList.add(cartproductvo);
            }
        }
        cartvo.setCartTotalPrice(cartTotalPrice);
        cartvo.setCartproductvoList(cartproductvoList);
        cartvo.setAllChecked(this.isallchecked(userid));
        cartvo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartvo;
    }

    private boolean isallchecked(Integer userid){
        if(userid==null){
        return false;
        }
        return cartMapper.selectcartproductcheckstatus(userid) == 0;

    }

}
