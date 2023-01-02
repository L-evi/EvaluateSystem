package com.project.evaluate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.mapper.FacultyMapper;
import com.project.evaluate.util.JwtUtil;

import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedisCache redisCache;

    //    方法已经弃用
    @Deprecated
    @Override
    public ResponseResult userLogin(Faculty faculty) {
//        从redis中获取信息
        Faculty tmp = JSONObject.toJavaObject(redisCache.getCacheObject("Faculty:" + faculty.getUserID()), Faculty.class);
        if (Objects.isNull(tmp)) {
            tmp = facultyMapper.selectByUserID(faculty.getUserID());
        }
        JSONObject jsonObject = new JSONObject();
//        如果对象为空则登录失败
        if (Objects.isNull(tmp)) {
            throw new UnknownAccountException("用户不存在");
        }
//        如果状态为1则禁用
        if (tmp.getStatus() == 1) {
            jsonObject.put("msg", "登录失败，账户状态异常，请联系管理员");
            return new ResponseResult(ResultCode.ACCOUNT_ERROR, jsonObject);
        }
        try {
            Md5Hash md5Hash = new Md5Hash(faculty.getPassword(), faculty.getUserID(), 1024);
            String password = md5Hash.toHex();
            if (password.equals(tmp.getPassword())) {
                jsonObject.clear();
                jsonObject.put("userID", tmp.getUserID());
                jsonObject.put("roleType", tmp.getRoleType());
                String token = JwtUtil.createJwt(String.valueOf(jsonObject), 60 * 60 * 1000 * 3L);
                jsonObject.clear();
                jsonObject = JSONObject.parseObject(JSON.toJSONString(tmp));
//                放入到redis中
                redisCache.setCacheObject("Faculty:" + tmp.getUserID(), tmp, 3, TimeUnit.HOURS);
                redisCache.setCacheObject("token:" + tmp.getUserID(), token, 3, TimeUnit.HOURS);
                jsonObject.put("token", token);
                jsonObject.put("msg", "登录成功");
                jsonObject.remove("password");
                return new ResponseResult(ResultCode.SUCCESS, jsonObject);
            } else {
                throw new IncorrectCredentialsException("密码错误");
            }
        } catch (IncorrectCredentialsException e) {
            e.printStackTrace();
            jsonObject.clear();
            jsonObject.put("msg", "密码错误");
            return new ResponseResult(ResultCode.LOGIN_ERROR, jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.clear();
            jsonObject.put("msg", "服务器错误");
            return new ResponseResult(ResultCode.SERVER_ERROR, jsonObject);
        }

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
//            将信息放入redis中
            redisCache.setCacheObject("Faculty:" + faculty.getUserID(), faculty, 3, TimeUnit.HOURS);
            return new ResponseResult(ResultCode.SUCCESS);
        } else {
            return new ResponseResult(ResultCode.DATABASE_ERROR);
        }
    }
}
