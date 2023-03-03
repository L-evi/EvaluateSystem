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
import com.project.evaluate.listener.CourseDataListener;
import com.project.evaluate.service.CourseService;

import com.project.evaluate.util.Pager;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public ResponseResult selectCourseByID(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        Course course = JSONObject.toJavaObject(redisCache.getCacheObject("Course:" + ID), Course.class);
        if (Objects.isNull(course)) {
            course = courseDao.selectByID(ID);
            if (Objects.isNull(course)) {
                jsonObject.put("msg", "查询结果为空");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
            redisCache.setCacheObject("Course:" + course.getID(), course, 1, TimeUnit.DAYS);
        }
        jsonObject = JSONObject.parseObject(JSON.toJSONString(course));
        jsonObject.put("msg", "查询成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectCourseByCourseID(Integer page, Integer pageSize, String courseID) {
        JSONObject jsonObject = new JSONObject();
        /**
         * 从redis中取出数据并通过流转化实体类List
         */
        List<Course> courses = redisCache.getCacheList("Course:" + courseID)
                .stream()
                .map(obj -> JSON.parseObject(JSON.toJSONString(obj), Course.class))
                .collect(Collectors.toList());
        if (Objects.isNull(courses) || courses.isEmpty()) {
            courses = courseDao.selectByCourseID(courseID);
            if (Objects.isNull(courses) || courses.isEmpty()) {
                jsonObject.put("msg", "查询结果为空");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
            /**
             * 全部加入到redis中
             * 之所以要先删除再加入，是因为redisCache中的setCacheList方法是在后面追加
             */
            courses.stream().forEach(course -> redisCache.setCacheObject("Course:" + course.getID(), course, 1, TimeUnit.DAYS));
            redisCache.deleteObject("Course:" + courseID);
            redisCache.setCacheList("Course:" + courseID, courses, 1, TimeUnit.DAYS);
        }
        /**
         * 自定义工具类分页
         */
        Pager<Course> pager = new Pager<>();
        PageInfo<Course> coursePageInfo = pager.getListPage(courses, page, pageSize);
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(coursePageInfo.getList()));
        /**
         * 返回值
         */
        jsonObject.put("msg", "查询成功");
        jsonObject.put("pages", coursePageInfo.getPages());
        jsonObject.put("total", coursePageInfo.getTotal());
        jsonObject.put("array", jsonArray);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult insertCourse(Course course) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.nonNull(redisCache.getCacheObject("Course:" + course.getCourseID()))) {
            jsonObject.put("msg", "数据重复");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        Integer num = courseDao.insertCourse(course);
        if (num < 1) {
            jsonObject.put("msg", "插入失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        redisCache.setCacheObject("Course:" + course.getID(), course, 1, TimeUnit.DAYS);
        redisCache.setCacheObject("Course:" + course.getCourseID(), course, 1, TimeUnit.DAYS);
        jsonObject.put("msg", "插入成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult updateCourse(Course course) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.nonNull(course.getID())) {
            redisCache.deleteObject("Course:" + course.getID());
        }
        if (Objects.nonNull(course.getCourseID())) {
            redisCache.deleteObject("Course:" + course.getCourseID());
        }
        Integer num = courseDao.updateCourse(course);
        /**
         *
         */
        if (num < 1) {
            jsonObject.put("msg", "更新失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        course = courseDao.selectByID(course.getID());
        redisCache.setCacheObject("Course:" + course.getID(), course);
        redisCache.setCacheObject("Course:" + course.getCourseID(), course);
        jsonObject.put("msg", "更新成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult deleteCourse(Integer ID, String courseID) {
        JSONObject jsonObject = new JSONObject();
        redisCache.deleteObject("Course:" + ID);
        redisCache.deleteObject("Course:" + courseID);
        Boolean isDelete = courseDao.deletaByID(ID);
        if (!isDelete) {
            jsonObject.put("msg", "删除失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        jsonObject.put("msg", "删除成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult importExcelCourse(String filename) {
        JSONObject jsonObject = new JSONObject();
        if (!new File(filename).exists()) {
            jsonObject.put("msg", "参数错误，无法读取文件");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        }
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
