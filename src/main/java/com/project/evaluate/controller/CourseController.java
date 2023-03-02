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
    @DataLog(modelName = "分页查询课程信息", operationType = "select")
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

    @GetMapping("/get/single/id")
    @DataLog(modelName = "查询课程信息", operationType = "select")
    public ResponseResult selectCourseByID(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID.equals(0)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseService.selectCourseByID(ID);
    }

    @GetMapping("/get/single/courseID")
    @DataLog(modelName = "查询课程信息", operationType = "select")
    public ResponseResult selectCourseByCourseID(Integer page, Integer pageSize, String courseID) {
        JSONObject jsonObject = new JSONObject();
        if (!Strings.hasText(courseID)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize.equals(0)) {
            pageSize = 10;
        }
        return courseService.selectCourseByCourseID(page, pageSize, courseID);
    }

    @PostMapping("/add")
    @DataLog(modelName = "添加课程", operationType = "insert")
    public ResponseResult insertCourse(@RequestBody Course course) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(course)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseService.insertCourse(course);
    }

    @PutMapping("/update")
    @DataLog(modelName = "修改课程信息", operationType = "update")
    public ResponseResult updateCourse(@RequestBody Course course) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(course) || Objects.isNull(course.getID())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseService.updateCourse(course);
    }

    @DeleteMapping("/delete")
    @DataLog(modelName = "删除课程信息", operationType = "delete")
    public ResponseResult deleteCourse(@RequestBody Course course) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(course) || Objects.isNull(course.getID()) || Objects.isNull(course.getCourseID())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseService.deleteCourse(course.getID(), course.getCourseID());
    }

    @PostMapping("/import/excel")
    @DataLog(modelName = "批量导入课程", operationType = "insert")
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
