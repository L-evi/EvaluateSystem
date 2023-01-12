package com.project.evaluate.service.impl;

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
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
    public ResponseResult deleteTeachingDocuments(int ID) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        map.put("taskID", ID);
        map.put("page", 1);
        map.put("pageSize", 10);
        List<CourseDocDetail> courseDocDetails = this.courseDocDetailDao.selectByTaskID(map);
//        教学文档任务已经上传文件了
        if (!courseDocDetails.isEmpty()) {
            jsonObject.put("msg", "教学文档文件已上传文件，无法删除");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        CourseDocTask courseDocTask = this.courseDocTaskDao.selectByID(ID);
        Date now = new Date();
//        如果任务超时 或者 任务已经关闭了
        if (now.after(courseDocTask.getDeadline()) || courseDocTask.getCloseTask() == 1) {
            jsonObject.put("msg", "任务已经过期或已经关闭，无法删除");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        没有问题了才删除
        Long num = this.courseDocTaskDao.deleteTaskByID(ID);
        if (num > 0) {
            jsonObject.put("msg", "删除成功");
            jsonObject.put("count", num);
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        }
        return new ResponseResult(ResultCode.DATABASE_ERROR);
    }
}
