package com.project.evaluate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.dao.FacultyDao;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.service.TokenService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.Claims;
import org.apache.shiro.authc.UnknownAccountException;
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
    private FacultyDao facultyDao;

    @Resource
    private RedisCache redisCache;

    @Override
    public ResponseResult getTokenMessage(String token) {
        JSONObject jsonObject = new JSONObject();
//        解析Token
        try {
//            查看token是否过期
            if (JwtUtil.isTimeout(token)) {
                jsonObject.put("msg", "token已过期");
                return new ResponseResult(ResultCode.LOGIN_TIMEOUT, jsonObject);
            }
            Claims claims = JwtUtil.parseJwt(token);
            jsonObject = JSONObject.parseObject(claims.getSubject());
            if (jsonObject.containsKey("userID")) {
                String userID = jsonObject.get("userID").toString();
                jsonObject.clear();
//                查看redis中是否有这个token
                if (!token.equals(this.redisCache.getCacheObject("token:" + userID))) {
                    jsonObject.put("msg", "登录超时");
                    return new ResponseResult(ResultCode.LOGIN_TIMEOUT, jsonObject);
                }
//                调用redis
                Faculty faculty = JSONObject.toJavaObject(this.redisCache.getCacheObject("Faculty:" + userID), Faculty.class);
                if (Objects.isNull(faculty)) {
                    faculty = this.facultyDao.selectByUserID(userID);
                    if (Objects.isNull(faculty)) {
                        throw new UnknownAccountException("账户不存在");
                    }
                    this.redisCache.setCacheObject("Faculty:" + userID, faculty, 1, TimeUnit.DAYS);
                }
                jsonObject = JSONObject.parseObject(JSON.toJSONString(faculty));
//                    去掉用户密码
                if (jsonObject.containsKey("password")) {
                    jsonObject.remove("password");
                }
                jsonObject.put("msg", "token获取信息成功");
//                    将token放入redis中：根据过期时间设置redis中的token过期时间
                int seconds = (int) (claims.getExpiration().getTime() - (new Date()).getTime());
                this.redisCache.setCacheObject("token:" + userID, token, seconds, TimeUnit.MILLISECONDS);
                return new ResponseResult(ResultCode.SUCCESS, jsonObject);
            }
        } catch (Exception e) {
            System.out.println("TokenService Token的Parse失败");
            jsonObject.put("msg", "Token 的解析失败");
            return new ResponseResult(ResultCode.SERVER_ERROR, jsonObject);
        }
        return null;
    }
}
