package com.legou.dao;

import com.legou.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    //检查用户名存不存在
    int checkusername(String username);
    //检查注册邮箱存不存在
    int checkemail(String email);
    //检测问题答案
    int checkanswer(@Param("username") String username,@Param("question") String question,@Param("answer") String answer);
    //检测登录信息
    User userlogin(@Param("username") String username,@Param("password")  String password);
    //查找用户的忘记密码答案
    String userquestion(String username);
    //重置密码
    int resetpassword(@Param("username") String username,@Param("passwordnew") String passwordnew);
    //校验密码
    int checkpassword(@Param("password")String password,@Param("userid")Integer userid);
    //校验邮箱（通过用户id）
    int checkemailbyuserid(@Param("userid")Integer userid,@Param("email")String email);
}