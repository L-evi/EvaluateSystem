package com.project.evaluate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.mapper.FacultyMapper;
import com.project.evaluate.util.JwtUtil;

import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
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
public class FacultyServiceImpl implements FacultyService {

    @Autowired()
    private FacultyMapper facultyMapper;



    //    方法已经弃用
    @Deprecated
    @Override
    public ResponseResult userLogin(Faculty faculty) {
        Faculty tmp = facultyMapper.selectByUserID(faculty.getUserID());
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
            String password = encoder.encodeToString(md5.digest(faculty.getPassword().getBytes("utf-8")));
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


    @Override
    public ResponseResult userRegister(Faculty faculty) {
//        如果在redis和数据库中找到了该数据，则说明已经注册了

        if (facultyMapper.selectByUserID(faculty.getUserID()) != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "用户已注册");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        明文密码进行MD5 + salt + 散列，盐就用userID
        Md5Hash md5Hash = new Md5Hash(faculty.getPassword(), faculty.getUserID(), 1024);
        faculty.setPassword(md5Hash.toHex());
        if (facultyMapper.addFaculty(faculty) == 1) {
            return new ResponseResult(ResultCode.SUCCESS);
        } else {
            return new ResponseResult(ResultCode.DATABASE_ERROR);
        }
    }
}
