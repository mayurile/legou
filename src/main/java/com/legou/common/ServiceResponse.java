package com.legou.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/1.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候，null对象，key会消失
public class ServiceResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    //构造方法
    private ServiceResponse(int status){
        this.status=status;
    }
    private ServiceResponse(int status,T data){
        this.status=status;
        this.data=data;
    }
    private ServiceResponse(int status,String msg,T data){
        this.status=status;
        this.msg=msg;
        this.data=data;
    }
    private ServiceResponse(int status,String msg){
        this.status=status;
        this.msg=msg;
    }
    @JsonIgnore
    //保证不在json序列化结果中
    public boolean issuccess(){
        return this.status==ResponseCode.SUCCESS.getcode();
    }
    public int getStatus(){
        return status;
    }
    public T getData(){
        return data;
    }
    public String getMsg(){
        return  msg;
    }
    //创建一个对象，通过一个成功的状态
    public static <T> ServiceResponse<T> createbysuccess(){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getcode());
    }
    //返回一个信息加成功的状态
    public static <T> ServiceResponse<T> createbysuccessmsg(String msg){
        return  new ServiceResponse<T>(ResponseCode.SUCCESS.getcode(),msg);
    }
    public static <T> ServiceResponse<T> createbysuccess(T data){
        return new ServiceResponse<T>(ResponseCode.SUCCESS.getcode(),data);
    }
    public static <T> ServiceResponse<T> createbysuccess(String msg,T data){
        return  new ServiceResponse<T>(ResponseCode.SUCCESS.getcode(),msg,data);
    }
    public static <T> ServiceResponse<T> createbyerror(){
        return new ServiceResponse<T>(ResponseCode.ERROR.getcode());
    }
    public static <T> ServiceResponse<T> createbyerror(String msg){
        return new ServiceResponse<T>(ResponseCode.ERROR.getcode(),msg);
    }
    //将参数当作变量方便拓展
    public static <T> ServiceResponse<T>  createbyerrorcodemessage(int errorCode,String errorMessage){
        return new ServiceResponse<T>(errorCode,errorMessage);
    }

}
