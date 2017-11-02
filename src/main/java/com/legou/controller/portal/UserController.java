package com.legou.controller.portal;

import com.legou.common.Const;
import com.legou.common.ResponseCode;
import com.legou.common.ServiceResponse;
import com.legou.pojo.User;
import com.legou.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

/**
 * Created by Administrator on 2017/11/1.
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private IUserService iUserService;
    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> Userlogin(String username, String password, HttpSession session){
        ServiceResponse<User> response=iUserService.login(username,password);
        //登录成功的话将用户放入session中
        if(response.issuccess()){
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }

    /**
     * 注销
     * @param session
     * @return
     */
    @RequestMapping(value = "loginout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> Logout(HttpSession session){
        //将当前用户删除掉
        session.removeAttribute(Const.CURRENT_USER);
        return  ServiceResponse.createbysuccess();
    }

    /**
     * 注册新用户
     * @param user
     * @return
     */
    @RequestMapping(value = "register.do",method = RequestMethod.POST)
    @ResponseBody
    public  ServiceResponse<String> register(User user){
    return iUserService.register(user);
    }

    /**
     * 用来校验用户，防止横向越权
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "checkvalid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> Checkvalid(String str,String type){
    return  iUserService.Checkvalid(str,type);
    }

    /**
     * 获取用户的信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getuserinfo(HttpSession session){
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        //成功获取
        if(user!=null){
            return ServiceResponse.createbysuccess(user);
        }
        //不成功
        return ServiceResponse.createbyerror("用户未登录，无法获取信息");

    }

    /**
     * 校验返回忘记密码的答案
     * @param username
     * @return
     */
    @RequestMapping(value = "forgetquestion.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetquestion(String username) {
        return iUserService.Forgetquestion(username);
    }

    /**
     * 校验问题答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forgetcheckanswer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetcheckanswer(String username,String question,String answer){
        return iUserService.Checkanswer(username,question,answer);
    }
    @RequestMapping(value = "forgetresetpassword.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> forgetresetpassword(String username,String passwordNew,String forgetToken){
        return iUserService.Resetpassword(username,passwordNew,forgetToken);
    }
    @RequestMapping(value = "resetpassword.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<String> resetpassword(HttpSession session,String passwordNew,String passwordold){
        //从session里去User对象（强转换）
        User user=(User)session.getAttribute(Const.CURRENT_USER);
        //校验是否登录
        if(user==null){
            return  ServiceResponse.createbyerror("用户未登录");
        }
        return iUserService.ResetPasswordOL(user,passwordNew,passwordold);
    }
    @RequestMapping(value = "updateinfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> updateinfo(HttpSession session,User user){
        //校验登录，只有在登录情况下才能更新用户信息
        User currentuser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentuser==null){
            return  ServiceResponse.createbyerror("用户未登录");
        }
        user.setId(currentuser.getId());
        ServiceResponse<User> response=iUserService.updateinfo(user);
        //如果更新成功，将user赋予用户名并返回给前端
        if(response.issuccess()){
            response.getData().setUsername(currentuser.getUsername());
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
    @RequestMapping(value = "getinfo.do",method = RequestMethod.POST)
    @ResponseBody
    public ServiceResponse<User> getinfo(HttpSession session,User user){
        //校验登录，如果未登录需要强制登录
        User currentuser=(User)session.getAttribute(Const.CURRENT_USER);
        if(currentuser==null){
            return  ServiceResponse.createbyerrorcodemessage(ResponseCode.NEED_LOGIN.getcode(),"未登录，需要强制登录");
        }
        return iUserService.getinfo(currentuser.getId());
    }
}
