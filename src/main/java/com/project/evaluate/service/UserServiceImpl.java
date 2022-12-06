package com.project.evaluate.service;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.User;
import com.project.evaluate.mapper.UserMapper;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 02:07
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired()
    private UserMapper userMapper;

    @Override
    public ResponseResult userLogin(User user) {
        User tmp = userMapper.selectByUsername(user.getUsername());
        String password = tmp.getPassword();
//        验证成功，返回token以及成功信息
        if (password.equals(user.getPassword())) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("username", user.getUsername());
            jsonObject.put("role", tmp.getRole());
            String token = JwtUtil.createJwt(String.valueOf(jsonObject));
            jsonObject.clear();
            jsonObject.put("token", token);
            jsonObject.put("msg", "登录成功");
            jsonObject.put("role", tmp.getRole());
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        } else {
            return new ResponseResult(ResultCode.LOGIN_ERROR);
        }
    }
}
