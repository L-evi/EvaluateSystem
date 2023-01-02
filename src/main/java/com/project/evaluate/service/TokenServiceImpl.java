package com.project.evaluate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.mapper.FacultyMapper;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/20 01:03
 */
@Service
public class TokenServiceImpl implements TokenService {

    @Resource
    private FacultyMapper facultyMapper;

    @Resource
    private RedisCache redisCache;

    @Override
    public ResponseResult getTokenMessage(String token) {
//        解析Token
        try {
//            查看token是否过期
            if (JwtUtil.isTimeout(token)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("msg", "token已过期");
                return new ResponseResult(ResultCode.LOGIN_TIMEOUT, jsonObject);
            }
            Claims claims = JwtUtil.parseJwt(token);
            JSONObject jsonObject = JSONObject.parseObject(claims.getSubject());
            if (jsonObject.containsKey("userID")) {
                String userID = jsonObject.get("userID").toString();
//                调用redis
                Faculty faculty = JSONObject.toJavaObject(redisCache.getCacheObject("Faculty:" + userID), Faculty.class);
                if (Objects.isNull(faculty)) {
                    faculty = facultyMapper.selectByUserID(userID);
                }
//                如果对象为空则返回错误
                if (!Objects.isNull(faculty)) {
                    jsonObject = JSONObject.parseObject(JSON.toJSONString(faculty));
//                    去掉用户密码
                    if (jsonObject.containsKey("password")) {
                        jsonObject.remove("password");
                    }
                    jsonObject.put("msg", "token获取信息成功");
//                    将信息放入redis中：根据过期时间设置redis中的token过期时间
                    int seconds = (int) (claims.getExpiration().getTime() - (new Date()).getTime());
                    redisCache.setCacheObject("Faculty:" + userID, faculty, seconds, TimeUnit.MILLISECONDS);
                    return new ResponseResult(ResultCode.SUCCESS, jsonObject);
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put("msg", "无法查询到该" + userID + "的信息");
                    return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
                }
            }
        } catch (Exception e) {
            System.out.println("TokenService Token的Parse失败");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "Token 的解析失败");
            return new ResponseResult(ResultCode.SERVER_ERROR, jsonObject);
        }
        return null;
    }
}
