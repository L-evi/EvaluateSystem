package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.Course;
import com.project.evaluate.service.CourseService;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:07
 */
@RestController
@RequestMapping(value = "/api/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    @GetMapping("/get/page")
    @DataLog(modelName = "查询课程信息", operationType = "select")
    public ResponseResult selectPageCourse(Course course, Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        if (!Strings.hasText(orderBy)) {
            orderBy = "ID ASC";
        }

        return courseService.selectPageCourse(course, page, pageSize, orderBy);
    }

    @PostMapping("/import/excel")
    public ResponseResult importExcelCourse(@RequestBody Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        if (!map.containsKey("filename")) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String filename = (String) map.get("filename");
        return courseService.importExcelCourse(filename);
    }
}
