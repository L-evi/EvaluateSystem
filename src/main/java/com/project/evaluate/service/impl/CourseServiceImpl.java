package com.project.evaluate.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.CourseDao;
import com.project.evaluate.entity.Course;
import com.project.evaluate.service.CourseService;
import com.project.evaluate.util.CourseDataListener;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:10
 */
@Service
public class CourseServiceImpl implements CourseService {
    @Resource
    private CourseDao courseDao;

    @Resource
    private RedisCache redisCache;

    @Override
    public ResponseResult selectPageCourse(Course course, Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(page, pageSize, orderBy);
        List<Course> courses = courseDao.selectPageCourse(course);
        if (Objects.isNull(courses) || courses.isEmpty()) {
            jsonObject.put("msg", "查询课程失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<Course> coursesInfo = new PageInfo<>(courses);
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(coursesInfo.getList()));
        jsonObject.put("total", coursesInfo.getTotal());
        jsonObject.put("pages", coursesInfo.getPages());
        jsonObject.put("array", jsonArray);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult importExcelCourse(String filename) {
        JSONObject jsonObject = new JSONObject();
        filename = "D:\\levi\\EvaluateSystem\\src\\main\\resources\\static\\excel\\course_write_1676884515154.xlsx";
        if (!new File(filename).exists()) {
            jsonObject.put("msg", "参数错误，无法读取文件");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        }
        // todo: 使用EasyExcel读取文件
        try (ExcelReader excelReader = EasyExcel.read(filename, Course.class, new CourseDataListener()).build()) {
            // 构建一个sheet可以指定名称或者no
            ReadSheet readSheet = EasyExcel.readSheet().build();
            excelReader.read(readSheet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ResponseResult(ResultCode.SUCCESS);
    }
}
