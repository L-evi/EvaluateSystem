package com.project.evaluate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.User;
import com.project.evaluate.mapper.UserMapper;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;

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
        User tmp = userMapper.selectByUsername(user.getUserID());
//        如果对象为空则登录失败
        if (Objects.isNull(tmp)) {
            return new ResponseResult(ResultCode.LOGIN_ERROR);
        }
//        如果密码为空则登录失败
        if (!Strings.hasText((tmp.getPassword()))) {
            return new ResponseResult(ResultCode.LOGIN_ERROR);
        }
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            Base64.Encoder encoder = Base64.getEncoder();
            String password = encoder.encodeToString(md5.digest(user.getPassword().getBytes("utf-8")));
            if (password.equals(tmp.getPassword())) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userID", tmp.getUserID());
                jsonObject.put("roleType", tmp.getRoleType());
                String token = JwtUtil.createJwt(String.valueOf(jsonObject));
                jsonObject.clear();
                jsonObject = JSONObject.parseObject(JSON.toJSONString(tmp));
                jsonObject.put("token", token);
                jsonObject.put("msg", "登录成功");
                jsonObject.remove("password");
                return new ResponseResult(ResultCode.SUCCESS, jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(ResultCode.SERVER_ERROR);
        }
        return new ResponseResult(ResultCode.SERVER_ERROR);
    }
}
