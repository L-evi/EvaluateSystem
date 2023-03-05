package com.project.evaluate.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    @Override
    public ResponseResult exportCourseDocTask(CourseDocTask courseDocTask) {
        JSONObject jsonObject = new JSONObject();
        String filename = tempPrePath + File.separator + System.currentTimeMillis() + "_CourseDocTask.xlsx";
        List<Map<String, Object>> collect = courseDocTaskDao.selectPageCourseDocTask(courseDocTask);
        EasyExcel.write(filename).head(getExcelHead()).sheet("课程任务").doWrite(collect);
        return null;
    }

    private List<List<String>> getExcelHead() {
        List<String> ids = ListUtils.newArrayList();
        ids.add("id");
        List<String> name = ListUtils.newArrayList();
        name.add("name");
        List<String> age = ListUtils.newArrayList();
        name.add("age");
        List<List<String>> head = ListUtils.newArrayList(ids, name, age);
        return head;
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
