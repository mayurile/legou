package com.legou.controller.portal;

import com.legou.common.Const;
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
    @RequestMapping(value = "loginout.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> Logout(HttpSession session){
        //将当前用户删除掉
        session.removeAttribute(Const.CURRENT_USER);
        return  ServiceResponse.createbysuccess();
    }
    @RequestMapping(value = "register.do",method = RequestMethod.GET)
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
    @RequestMapping(value = "checkvalid.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> Checkvalid(String str,String type){
    return  iUserService.Checkvalid(str,type);
    }

    /**
     * 获取用户的信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do",method = RequestMethod.GET)
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
    @RequestMapping(value = "forgetquestion.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> forgetquestion(String username) {
        return iUserService.Forgetquestion(username);
    }
    @RequestMapping(value = "forgetcheckanswer.do",method = RequestMethod.GET)
    @ResponseBody
    public ServiceResponse<String> forgetcheckanswer(String username,String question,String answer){
        return iUserService.Checkanswer(username,question,answer);
    }
}
