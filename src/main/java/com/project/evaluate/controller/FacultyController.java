package com.project.evaluate.controller;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.service.FacultyService;
import com.project.evaluate.util.IPUtil;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@RequestMapping(value = "/api/faculty")
@CrossOrigin(value = "*")
public class FacultyController {

    @Resource
    private RedisCache redisCache;

    @Resource
    private FacultyService facultyService;

    @DataLog(modelName = "用户登录", operationType = "login")
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseResult userLogin(@RequestBody Map<String, Object> dataMap, HttpServletRequest request) {
//        获取其中的数据
        Faculty faculty = new Faculty();
        faculty.setUserID((String) dataMap.get("userID"));
        faculty.setPassword((String) dataMap.get("password"));
        faculty.setLoginIP(IPUtil.getIPAddress(request));
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
        if (!Strings.hasText(faculty.getUserID()) || !Strings.hasText(faculty.getPassword())) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "账号或密码不能为空");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        faculty.setLastLoginIP(IPUtil.getIPAddress(request));
        faculty.setLastLoginTime(new DateTime(TimeZone.getTimeZone("Asia/Shanghai")));
        faculty.setLoginTime(new DateTime());
        faculty.setLoginIP(IPUtil.getIPAddress(request));
        faculty.setIsInitPwd(0);
        return this.facultyService.userRegister(faculty);
    }

    @PostMapping("/manage/add")
    @RequiresRoles("1")
    public ResponseResult addFaculty(@RequestBody Faculty faculty) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(faculty)
                || !Strings.hasText(faculty.getUserID())
                || Objects.isNull(faculty.getRoleType())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        if (!Strings.hasText(faculty.getUserName())) {
            faculty.setUserName(faculty.getUserID());
        }
        faculty.setPassword(faculty.getUserID());
        faculty.setStatus(0);
        faculty.setIsInitPwd(1);
        return this.facultyService.insertFaculty(faculty);
    }

    @PutMapping("/manage/update")
    @RequiresRoles("1")
    public ResponseResult updateFaculty(@RequestBody Faculty faculty) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(faculty)
                || !Strings.hasText(faculty.getUserID())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        faculty.setPassword(null);
        return this.facultyService.updateFaculty(faculty);
    }

    @PutMapping("/manage/reset")
    @RequiresRoles("1")
    public ResponseResult resetFaculty(String userID) {
        JSONObject jsonObject = new JSONObject();
        if (!Strings.hasText(userID)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.facultyService.resetFaculty(userID);
    }

    @GetMapping("/manage/get/page")
    @RequiresRoles("1")
    @DataLog(modelName = "用户管理", operationType = "select")
    public ResponseResult selectPageFaculty(Faculty faculty, Integer page, Integer pageSize, String orderBy) {
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        if (!Strings.hasText(orderBy)) {
            orderBy = "userID ASC";
        }
        return this.facultyService.selectPageFaculty(faculty, page, pageSize, orderBy);
    }

    @DeleteMapping("/manage/delete")
    @RequiresRoles("1")
    public ResponseResult deleteFaculty(String userID) {
        JSONObject jsonObject = new JSONObject();
        if (!Strings.hasText(userID)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.facultyService.deleteFaculty(userID);
    }

    @PostMapping(value = "/personal/update")
    public ResponseResult personalMessageUpdate(@RequestBody Faculty faculty, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String token = request.getHeader("token");
        if (Objects.isNull(faculty)
                || !Strings.hasText(token)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
//        获取userID
        String userID = null;
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            userID = (String) jsonObject.get("userID");
            jsonObject.clear();
        } catch (Exception e) {
            throw new RuntimeException("token parse失败");
        }
//        设置只有用户才能更改的数据
        Faculty temp = new Faculty();
        temp.setUserID(userID);
        temp.setEmail(faculty.getEmail());
        temp.setMobile(faculty.getMobile());
        temp.setMemo(faculty.getMemo());
        return this.facultyService.updateFaculty(temp);
    }

    @PostMapping(value = "/personal/password/reset")
    public ResponseResult personalPasswordReset(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String userID = null;
        String token = request.getHeader("token");
//        解析token
        try {
            userID = (String) JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject()).get("userID");
        } catch (Exception e) {
            throw new RuntimeException("token parse 错误");
        }
        String password = (String) map.get("password");
        String oldPassword = (String) map.get("oldPassword");
        if (!Strings.hasText(password) || !Strings.hasText(oldPassword) || !Strings.hasText(userID)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.facultyService.resetPassword(userID, oldPassword, password);
    }

}
