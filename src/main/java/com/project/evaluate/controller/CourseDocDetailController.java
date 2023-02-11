package com.project.evaluate.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.service.CourseDocDetailService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.ibatis.annotations.Delete;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
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

    @RequestMapping(value = "/delete/taskID")
    public ResponseResult deleteByTaskID(@RequestBody CourseDocDetail courseDocDetail, HttpServletRequest request) {
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
        if (courseDocDetail.getTaskID() != 0) {
            return this.courseDocDetailService.deleteByTaskID(courseDocDetail.getTaskID(), userID);
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
            jsonObject = JSONObject.parseObject(JSON.toJSONString(JwtUtil.parseJwt(token).getSubject()));
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

    @GetMapping(value = "/search/task-id")
    public ResponseResult selectByTaskID(Integer taskID, Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(taskID)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        if (Objects.isNull(orderBy)) {
            orderBy = "ID ASC";
        }
        return this.courseDocDetailService.selectByTaskID(taskID, page, pageSize, orderBy);
    }

}
