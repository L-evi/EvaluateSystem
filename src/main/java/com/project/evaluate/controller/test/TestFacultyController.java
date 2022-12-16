package com.project.evaluate.controller.test;

import cn.hutool.core.date.DateTime;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.mapper.FacultyMapper;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/16 16:08
 */

@RequestMapping("/test")
@RestController
@CrossOrigin(value = "*")
public class TestFacultyController {


    @Resource
    private FacultyMapper facultyMapper;

    @RequestMapping(value = "/faculty/add", method = RequestMethod.POST)
    public ResponseResult addFaculty(@RequestBody Faculty faculty) {
        String password = faculty.getPassword();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            Base64.Encoder encoder = Base64.getEncoder();
            password = encoder.encodeToString(md5.digest(password.getBytes("utf-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        faculty.setPassword(password);
        faculty.setLastLoginIP("localhost");
        faculty.setLastLoginTime(new DateTime(TimeZone.getTimeZone("Asia/Shanghai")));
        faculty.setLoginTime(new DateTime());
        faculty.setLoginIP("localhost");
        faculty.setIsInitPwd(0);
        System.out.println(faculty.toString());
        int i = facultyMapper.addFaculty(faculty);
        if (i == 1) {
            return new ResponseResult(ResultCode.SUCCESS);
        } else {
            return new ResponseResult(ResultCode.INVALID_PARAMETER);
        }
    }
}
