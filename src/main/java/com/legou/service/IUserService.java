package com.legou.service;

import com.legou.common.ServiceResponse;
import com.legou.pojo.User;

/**
 * Created by Administrator on 2017/11/1.
 */

public interface IUserService {
     ServiceResponse<User> login(String username, String password);
     ServiceResponse<String> register(User user);
    ServiceResponse<String> Checkvalid(String str,String type);
    ServiceResponse<String> Forgetquestion(String username);
    ServiceResponse<String> Checkanswer(String username,String question,String answer);
}
