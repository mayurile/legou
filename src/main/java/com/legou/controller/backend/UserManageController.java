package com.legou.controller.backend;

import com.legou.common.Const;
import com.legou.common.ServiceResponse;
import com.legou.pojo.User;
import com.legou.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import javax.swing.text.html.HTML;

/**
 * Created by Administrator on 2017/11/2.
 */
@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;
    @RequestMapping(value = "managelogin.do",method = RequestMethod.POST)
    public ServiceResponse<User> login(String username, String password, HttpSession session){
        ServiceResponse<User> response=iUserService.login(username,password);
        if(response.issuccess()){
            User user=response.getData();
            if(user.getRole()== Const.Role.ROLEMANGER){
                session.setAttribute(Const.CURRENT_USER,user);
                return response;
            }else{
                return ServiceResponse.createbyerror("不是管理员不能登录");
            }
        }
        return response;
    }
}
