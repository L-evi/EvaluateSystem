package com.project.evaluate.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Course;
import com.project.evaluate.service.CourseService;
import com.project.evaluate.util.ApplicationContextProvider;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:07
 */
@RestController
@CrossOrigin("*")
@RequestMapping(value = "/api/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    @GetMapping("/get/page")
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
    public ResponseResult selectCourseByID(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID.equals(0)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseService.selectCourseByID(ID);
    }

    @GetMapping("/get/single/courseID")
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
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    public ResponseResult insertCourse(@RequestBody Course course) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(course)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseService.insertCourse(course);
    }

    @PutMapping("/update")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    public ResponseResult updateCourse(@RequestBody Course course) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(course) || Objects.isNull(course.getID())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseService.updateCourse(course);
    }

    @DeleteMapping("/delete")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    public ResponseResult deleteCourse(@RequestBody Course course) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(course) || Objects.isNull(course.getID()) || Objects.isNull(course.getCourseID())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseService.deleteCourse(course.getID(), course.getCourseID());
    }

    @PostMapping("/excel/import")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    public ResponseResult importExcelCourse(@RequestBody Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        if (!map.containsKey("filename")) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String filename = (String) map.get("filename");
        return courseService.importExcelCourse(filename);
    }

    @GetMapping("/excel/template")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    public ResponseResult getCourseExcelTemplate() {
        JSONObject jsonObject = new JSONObject();
        String tempPreFilename = ApplicationContextProvider
                .getApplicationContext()
                .getEnvironment()
                .getProperty("temp-pre-path");
        String filename = tempPreFilename + File.separator + "Course_Template.xlsx";
        try {
            EasyExcel.write(filename, Course.class).sheet("template").doWrite(() -> {
                List<Course> courses = ListUtils.newArrayListWithCapacity(1);
                courses.add(new Course());
                return courses;
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        jsonObject.put("msg", "模板生成成功");
        jsonObject.put("filename", filename);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }
}
