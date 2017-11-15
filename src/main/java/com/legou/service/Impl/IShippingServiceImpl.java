package com.legou.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.dao.ShippingMapper;
import com.legou.pojo.Shipping;
import com.legou.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/11/11.
 */
@Service("iShippingService")
public class IShippingServiceImpl implements IShippingService {
    @Autowired
    private ShippingMapper shippingMapper;

    public ServiceResponse add(Integer userid, Shipping shipping){
        if(userid==null||shipping==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        shipping.setUserId(userid);
        int resultcount=shippingMapper.insert(shipping);
        //done 前端约定将id返回给前端，需要在insert之后将id拿出
        if(resultcount>0){
            Map result= Maps.newHashMap();
            result.put("shippingid",shipping.getId());
            return ServiceResponse.createbysuccess("收获地址添加成功",result);
        }else{
            return ServiceResponse.createbyerror("收获地址添加失败");
        }
    }
    public ServiceResponse delete(Integer userid,Integer id){
        if(userid==null||id==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        //这样写会横向越权,拿到别人的shippingid也能删除，再写一个sql方法，校验userid
//        int resultrow=shippingMapper.deleteByPrimaryKey(id);
//        if(resultrow>0){
//            return ServiceResponse.createbysuccessmsg("删除地址成功");
//        }else {
//            return ServiceResponse.createbyerror("删除地址失败");
//        }
        int resultrow=shippingMapper.deletebyidanduserid(userid,id);
        if(resultrow>0){
            return ServiceResponse.createbysuccessmsg("删除地址成功");
        }else {
            return ServiceResponse.createbyerror("删除地址失败");
        }
    }
    public ServiceResponse update(Integer userid,Shipping shipping){
        if(userid==null||shipping==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        shipping.setUserId(userid);
        int resultcount=shippingMapper.updatebyshipping(shipping);
        if(resultcount>0){
            return ServiceResponse.createbysuccessmsg("更新地址成功");
        }else {
            return ServiceResponse.createbyerror("更新地址错误");
        }
    }
    public ServiceResponse<Shipping> select(Integer userid,Integer shippingid){
        if(userid==null||shippingid==null){
            return ServiceResponse.createbyerrorcodemessage(ResponseCode.ILLEGAL_ARGUMENT.getcode(),ResponseCode.ILLEGAL_ARGUMENT.getdesc());
        }
        Shipping shipping=shippingMapper.selectbyshippingidanduserid(userid,shippingid);
        if(shipping==null){
            return ServiceResponse.createbyerror("无法查询到相应地址");
        }else {
            return ServiceResponse.createbysuccess(shipping);
        }
    }
    public ServiceResponse<PageInfo> list(Integer userid, int pagesize, int pagenum){
        PageHelper.startPage(pagenum,pagesize);
        List<Shipping> shippingList=shippingMapper.selectbyuserid(userid);
        PageInfo pageInfo=new PageInfo(shippingList);
        return ServiceResponse.createbysuccess(pageInfo);
    }
}
