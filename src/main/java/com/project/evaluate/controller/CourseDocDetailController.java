package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.service.CourseDocDetailService;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public ResponseResult deleteByTaskID(@RequestBody CourseDocDetail courseDocDetail) {
        if (courseDocDetail.getTaskId() != 0) {
            return this.courseDocDetailService.deleteByTaskID(courseDocDetail.getTaskId());
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "TaskID缺失，无法删除");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
    }

}
