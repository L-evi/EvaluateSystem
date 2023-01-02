package com.project.evaluate.controller;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.service.FacultyService;

import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.ws.Response;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 02:06
 */
@RestController
@RequestMapping(value = "/user")
@CrossOrigin(value = "*")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseResult userLogin(@RequestBody Map<String, Object> dataMap) {
//        获取其中的数据
        Faculty faculty = new Faculty();
        faculty.setUserID((String) dataMap.get("userID"));
        faculty.setPassword((String) dataMap.get("password"));
//        调用Service服务进行认证
        return facultyService.userLogin(faculty);
/*//        System.out.println(faculty.toString());
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(faculty.getUserID(), faculty.getPassword());
        JSONObject jsonObject = new JSONObject();
        try {
            subject.login(usernamePasswordToken);
            if (subject.isAuthenticated()) {
                jsonObject.put("msg", "登录成功");
            }
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        } catch (UnknownAccountException e) {
            System.out.println("账号错误");
            e.printStackTrace();
        } catch (IncorrectCredentialsException e) {
            System.out.println("密码错误");
            e.printStackTrace();
        }
        jsonObject.put("msg", "登录失败");
        return new ResponseResult(ResultCode.LOGIN_ERROR, jsonObject);*/
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseResult userLogout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return new ResponseResult(ResultCode.SUCCESS);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = "application/json")
    public ResponseResult userRegister(@RequestBody Faculty faculty) {
        if (!Strings.hasText(faculty.getUserID()) || !Strings.hasText(faculty.getPassword())) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "账号或密码不能为空");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        faculty.setLastLoginIP("localhost");
        faculty.setLastLoginTime(new DateTime(TimeZone.getTimeZone("Asia/Shanghai")));
        faculty.setLoginTime(new DateTime());
        faculty.setLoginIP("localhost");
        faculty.setIsInitPwd(0);
        return facultyService.userRegister(faculty);
    }
}
