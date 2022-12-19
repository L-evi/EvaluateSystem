package com.project.evaluate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.mapper.FacultyMapper;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

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

    @Override
    public ResponseResult getTokenMessage(String token) {
//        解析Token
        try {
            Claims claims = JwtUtil.parseJwt(token);
            JSONObject jsonObject = JSONObject.parseObject(claims.getSubject());
            if (jsonObject.containsKey("userID")) {
                String userID = jsonObject.get("userID").toString();
                Faculty faculty = facultyMapper.selectByUserID(userID);
//                如果对象为空则返回错误
                if (!Objects.isNull(faculty)) {
                    jsonObject = JSONObject.parseObject(JSON.toJSONString(faculty));
//                    去掉用户密码
                    if (jsonObject.containsKey("password")) {
                        jsonObject.remove("password");
                    }
                    jsonObject.put("msg", "token获取信息成功");
                    return new ResponseResult(ResultCode.SUCCESS, jsonObject);
                } else {
                    jsonObject = new JSONObject();
                    jsonObject.put("msg", "无法查询到该" + userID + "的信息");
                    return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
                }
            }
        } catch (Exception e) {
            System.out.println("TokenService Token的Parse失败");
            throw new RuntimeException(e);
        }

        return null;
    }
}
