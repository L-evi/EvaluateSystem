package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.service.CourseDocDetailService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:09
 */
@RestController
@CrossOrigin("*")
@RequestMapping(value = "/api/courseDocDetail/")
public class CourseDocDetailController {
    @Resource
    private CourseDocDetailService courseDocDetailService;

    @DeleteMapping(value = "/delete/taskID")
    @RequiresRoles(value = {"0", "2"}, logical = Logical.OR)
    @DataLog(modelName = "根据任务ID删除课程文档", operationType = "delete")
    public ResponseResult deleteByTaskID(Integer taskID, HttpServletRequest request) {
        String token = request.getHeader("token");
        String userID = null;
//        判断是否为教师，如果为教师则只能删除自己的
        try {
            JSONObject jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            if (jsonObject.get("roleType").equals("0")) {
                userID = (String) jsonObject.get("userID");
            }
        } catch (Exception e) {
            throw new RuntimeException("token parse 错误");
        }
        if (taskID != 0) {
            return this.courseDocDetailService.deleteByTaskID(taskID, userID);
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "TaskID缺失，无法删除");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
    }

    @RequiresRoles(value = {"0", "2"}, logical = Logical.OR)
    @DeleteMapping("/delete/id")
    @DataLog(modelName = "删除课程文档", operationType = "delete")
    public ResponseResult deleteByID(Integer ID, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID)) {
            jsonObject.put("msg", "ID缺失，无法删除");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String token = request.getHeader("token");
        String userID = null;
        Integer roleType = null;
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            userID = (String) jsonObject.get("userID");
            roleType = (Integer) jsonObject.get("roleType");
            if (!Strings.hasText(userID) || Objects.isNull(roleType)) {
                jsonObject.clear();
                jsonObject.put("msg", "参数缺失");
                return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
            }
        } catch (Exception e) {
            throw new RuntimeException("token parse 错误");
        }
        return this.courseDocDetailService.deleteByID(ID, roleType, userID);
    }

    @GetMapping(value = "/get/taskID")
    @DataLog(modelName = "根据任务ID查询课程文档", operationType = "select")
    public ResponseResult selectByTaskID(Integer taskID, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(taskID)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String token = request.getHeader("token");
        String userID = null;
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            // 教师只能看到自己的
            if (jsonObject.get("roleType").equals("0")) {
                userID = (String) jsonObject.get("userID");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.courseDocDetailService.selectByTaskID(taskID, userID);
    }

    @PostMapping(value = "/submit")
    @DataLog(operationType = "insert", modelName = "提交文档上传任务")
    public ResponseResult submit(@RequestBody CourseDocDetail courseDocDetail, HttpServletRequest request) throws IOException {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(courseDocDetail) || Objects.isNull(courseDocDetail.getDocPath()) || Objects.isNull(courseDocDetail.getTaskID()) || Objects.isNull(courseDocDetail.getDocTypeID())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        if (Objects.isNull(courseDocDetail.getSubmitter())) {
            String token = request.getHeader("token");
            try {
                jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
                if (jsonObject.containsKey("userID")) {
                    courseDocDetail.setSubmitter((String) jsonObject.get("userID"));
                } else {
                    throw new RuntimeException("token parse 错误");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (Objects.isNull(courseDocDetail.getUploadTime())) {
            courseDocDetail.setUploadTime(new Date());
        }
        return courseDocDetailService.submitDocument(courseDocDetail);
    }

}
