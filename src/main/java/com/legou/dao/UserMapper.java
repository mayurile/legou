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

    String userquestion(String username);
}