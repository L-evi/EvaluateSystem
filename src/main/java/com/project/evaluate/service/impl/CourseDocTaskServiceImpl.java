package com.project.evaluate.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.CourseDao;
import com.project.evaluate.dao.CourseDocDetailDao;
import com.project.evaluate.dao.CourseDocTaskDao;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.service.CourseDocTaskService;
import com.project.evaluate.handler.MapExcelHandler;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.io.File;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:11
 */
@Service
public class CourseDocTaskServiceImpl implements CourseDocTaskService {
    @Resource
    private CourseDocTaskDao courseDocTaskDao;

    @Resource
    private CourseDao courseDao;

    @Resource
    private CourseDocDetailDao courseDocDetailDao;

    @Resource
    private RedisCache redisCache;

    @Value("${file.temp-pre-path}")
    private String tempPrePath;

    @Override
    public ResponseResult selectPageCourseDocTask(CourseDocTask courseDocTask, Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(page, pageSize, orderBy);
        List<Map<String, Object>> courseDocTasks = this.courseDocTaskDao.selectPageCourseDocTask(courseDocTask);
        if (Objects.isNull(courseDocTasks) || courseDocTasks.isEmpty()) {
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<Map<String, Object>> mapPageInfo = new PageInfo<>(courseDocTasks);
        List<Map<String, Object>> list = mapPageInfo.getList();
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(list));
        jsonObject.put("array", jsonArray);
        jsonObject.put("total", mapPageInfo.getTotal());
        jsonObject.put("pages", mapPageInfo.getPages());
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult deleteTeachingDocuments(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        CourseDocDetail courseDocDetail = this.courseDocDetailDao.selectOneByTaskID(ID);
        if (Objects.nonNull(courseDocDetail)) {
            jsonObject.put("msg", "该任务已经提交了文档，无法删除");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        CourseDocTask courseDocTask = this.courseDocTaskDao.selectByID(ID);
//        如果任务超时 或者 任务已经关闭了
        if (courseDocTask.getDeadline().before(new Date()) || courseDocTask.getCloseTask() == 1) {
            jsonObject.put("msg", "任务已经过期或已经关闭，无法删除");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        没有问题了才删除
        Long num = this.courseDocTaskDao.deleteTaskByID(ID);
        if (num > 0) {
            jsonObject.put("msg", "删除成功");
            jsonObject.put("num", num);
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        }
        jsonObject.put("msg", "删除失败");
        return new ResponseResult(ResultCode.DATABASE_ERROR, jsonObject);
    }


    private static final Map<String, String> COLUMN_NAME_MAP = new HashMap<>();

    static {
        COLUMN_NAME_MAP.put("ID", "编号");
        COLUMN_NAME_MAP.put("courseID", "课程ID");
        COLUMN_NAME_MAP.put("courseName", "课程名称");
        COLUMN_NAME_MAP.put("operator", "操作者");
        COLUMN_NAME_MAP.put("teacher", "授课老师");
        COLUMN_NAME_MAP.put("studentNum", "学生数量");
        COLUMN_NAME_MAP.put("taskStatus", "任务状态");
        COLUMN_NAME_MAP.put("closeTask", "是否关闭任务");
        COLUMN_NAME_MAP.put("schoolStartYear", "开始学年");
        COLUMN_NAME_MAP.put("schoolEndYear", "结束学年");
        COLUMN_NAME_MAP.put("grades", "年级专业");
        COLUMN_NAME_MAP.put("schoolTerm", "学期");
        COLUMN_NAME_MAP.put("issueTime", "开始时间");
        COLUMN_NAME_MAP.put("deadline", "结束时间");
    }

    @Override
    public ResponseResult exportCourseDocTask(CourseDocTask courseDocTask) {
        JSONObject jsonObject = new JSONObject();
        String filename = tempPrePath + File.separator
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HHmm"))
                + "_CourseDocTask.xlsx";
        List<Map<String, Object>> courseDocTasks = courseDocTaskDao.selectPageCourseDocTask(courseDocTask);
        if (Objects.isNull(courseDocTasks) || courseDocTasks.isEmpty()) {
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        EasyExcel.write(filename).head(MapExcelHandler.getExcelHead(courseDocTasks.get(0), COLUMN_NAME_MAP))
                .sheet("test")
                .doWrite(MapExcelHandler.getExcelData(courseDocTasks));
        jsonObject.put("filename", filename);
        jsonObject.put("msg", "导出成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }


    @Override
    public ResponseResult updateCourseDocTask(CourseDocTask courseDocTask) {
        JSONObject jsonObject = new JSONObject();
        Boolean isUpdate = courseDocTaskDao.updateCourseDocTask(courseDocTask);
        if (!isUpdate) {
            jsonObject.put("msg", "更新数据失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        jsonObject.put("msg", "更新数据成功");
        redisCache.setCacheObject("CourseDocTask:" + courseDocTask.getID(), courseDocTask, 1, TimeUnit.DAYS);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }


    @Override
    public ResponseResult resetCourseDocTask(Integer ID, Integer status) {
        JSONObject jsonObject = new JSONObject();

        Boolean isUpdate = this.courseDocTaskDao.updateCourseDocTaskStatus(ID, status);
        if (!isUpdate) {
            jsonObject.put("msg", "更新数据失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        jsonObject.put("msg", "更新数据成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult insertCourseDocTask(@RequestBody List<CourseDocTask> courseDocTasks) {
        JSONObject jsonObject = new JSONObject();
        Integer num = courseDocTaskDao.insertPageCourseDocTask(courseDocTasks);
        if (num < 1) {
            jsonObject.put("msg", "插入数据失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        jsonObject.put("num", num);
        jsonObject.put("msg", "插入数据成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }
}
