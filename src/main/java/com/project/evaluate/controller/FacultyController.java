package com.project.evaluate.controller;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.service.FacultyService;
import com.project.evaluate.util.IPUtil;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 02:06
 */
@RestController
@RequestMapping(value = "/api/user")
@CrossOrigin(value = "*")
public class FacultyController {

    @Resource
    private RedisCache redisCache;

    @Resource
    private FacultyService facultyService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseResult userLogin(@RequestBody Map<String, Object> dataMap, HttpServletRequest request) {
//        获取其中的数据
        Faculty faculty = new Faculty();
        faculty.setUserId((String) dataMap.get("userID"));
        faculty.setPassword((String) dataMap.get("password"));
        faculty.setLoginIp(IPUtil.getIPAddress(request));
//        调用Service服务进行认证
        return this.facultyService.userLogin(faculty);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseResult userLogout() {
        Subject subject = SecurityUtils.getSubject();
        System.out.println("principal: " + subject.getPrincipal());
        String userID = (String) subject.getPrincipal();
        this.redisCache.deleteObject("Faculty:" + userID);
        this.redisCache.deleteObject("token:" + userID);
        subject.logout();
        return new ResponseResult(ResultCode.SUCCESS);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    public ResponseResult userRegister(@RequestBody Faculty faculty, HttpServletRequest request) {
        if (!Strings.hasText(faculty.getUserId()) || !Strings.hasText(faculty.getPassword())) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "账号或密码不能为空");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        faculty.setLastLoginIp(IPUtil.getIPAddress(request));
        faculty.setLastLoginTime(new DateTime(TimeZone.getTimeZone("Asia/Shanghai")));
        faculty.setLoginTime(new DateTime());
        faculty.setLoginIp(IPUtil.getIPAddress(request));
        faculty.setIsInitPwd(0);
        return this.facultyService.userRegister(faculty);
    }
}
