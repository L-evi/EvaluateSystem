package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.service.CourseDocDetailService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:09
 */
@RestController
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
        if (courseDocDetail.getTaskId() != 0) {
            return this.courseDocDetailService.deleteByTaskID(courseDocDetail.getTaskId(), userID);
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "TaskID缺失，无法删除");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
    }

    @RequestMapping(value = "/search/task-id")
    public ResponseResult selectByTaskID(@RequestBody Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        if (!map.containsKey("taskID")) {
            jsonObject.put("msg", "taskID缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.courseDocDetailService.selectByTaskID(map);
    }

}
