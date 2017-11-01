package com.legou.service.Impl;

import com.legou.common.Const;
import com.legou.common.ServiceResponse;
import com.legou.common.TokenCache;
import com.legou.dao.UserMapper;
import com.legou.pojo.User;
import com.legou.service.IUserService;
import com.legou.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by Administrator on 2017/11/1.
 */
@Service("iUserService")
public class IUserServiceimpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    //springscan扫描包的方式找到dao层

    @Override
    public ServiceResponse<User> login(String username, String password) {
        int resultcount = userMapper.checkusername(username);
        if (resultcount == 0) {
            return ServiceResponse.createbyerror("用户不存在!");
        }
        String md5password=MD5Util.MD5EncodeUtf8(password);
        User user = userMapper.userlogin(username, md5password);
        if (user == null) {
            return ServiceResponse.createbyerror("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServiceResponse.createbysuccess("登录成功", user);



    }

    /**
     * 用户输入信息进行注册
     * @param user
     * @return
     */
        @Override
        public ServiceResponse<String> register(User user){
        ServiceResponse validResponse=this.Checkvalid(user.getUsername(),Const.USERNAME);
        if(!validResponse.issuccess()){
            return validResponse;
        }
        validResponse=this.Checkvalid(user.getEmail(),Const.EMAIL);
            if(!validResponse.issuccess()){
                return validResponse;
            }
         //将注册的user里面的角色设置为顾客，并加密码字段用md5加密技术进行加密
         user.setRole(Const.Role.ROLECOSTOMMER);
         user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
         //输入到数据库中
        int resultcount=userMapper.insert(user);
         if(resultcount==0){
             return ServiceResponse.createbyerror("未知异常，注册失败");
         }
         return ServiceResponse.createbysuccessmsg("注册成功");
        }

    /**
     * 对用户输入的信息进行校验
     * @param str
     * @param type
     * @return
     */
        @Override
        public ServiceResponse<String> Checkvalid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            //开始校验
            if(Const.USERNAME.equals(type)){int resultcount =userMapper.checkusername(str);
                if(resultcount>0){
                    return  ServiceResponse.createbyerror("用户已存在");
                }
            }
            if(Const.EMAIL.equals(type)){
                int resultcount =userMapper.checkemail(str);
                if(resultcount>0){
                    return  ServiceResponse.createbyerror("邮箱已存在");
                }
            }
        }else {
            return ServiceResponse.createbyerror("参数错误");
        }
        return ServiceResponse.createbysuccessmsg("校验成功");
        }

    @Override
    public ServiceResponse<String> Forgetquestion(String username) {
        ServiceResponse validresponse =this.Checkvalid(username,Const.USERNAME);
        if(validresponse.issuccess()){
            return ServiceResponse.createbyerror("用户不存在");
        }
        String Question=userMapper.userquestion(username);
        if(StringUtils.isNotBlank(Question)){
            return ServiceResponse.createbysuccess(Question);
        }
        return  ServiceResponse.createbyerror("找回密码的问题是空的");

    }

    @Override
    public ServiceResponse<String> Checkanswer(String username, String question, String answer) {
        int resultcount =userMapper.checkanswer(username,question,answer);
        if(resultcount>0){
            String fortaken= UUID.randomUUID().toString();
            TokenCache.setkey("token_"+username,fortaken);
            return ServiceResponse.createbysuccess(fortaken);
        }
            return ServiceResponse.createbyerror("问题与答案有误！");
    }
}
