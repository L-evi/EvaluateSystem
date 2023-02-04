package com.project.evaluate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.CourseDao;
import com.project.evaluate.dao.CourseDocDetailDao;
import com.project.evaluate.dao.CourseDocTaskDao;
import com.project.evaluate.entity.Course;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.service.CourseDocTaskService;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
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

    @Override
    public ResponseResult submitDocument(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
//        获取prePath
        String prePath = (String) map.get("teachingDocRoot");
        String fileName = (String) map.get("FileName");
        Integer taskID = (Integer) map.get("taskID");
//        获取CourseDocTask信息
//        从redis中获取CourseDocTask信息
        CourseDocTask courseDocTask = JSONObject.toJavaObject(this.redisCache.getCacheObject("CourseDocTask:" + taskID), CourseDocTask.class);
        if (Objects.isNull(courseDocTask)) {
            courseDocTask = this.courseDocTaskDao.selectByID(taskID);
            if (Objects.isNull(courseDocTask)) {
                jsonObject.put("msg", "找不到课程文档任务信息");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            } else {
//                将信息放入redis中
                this.redisCache.setCacheObject("CourseDocTask:" + taskID, courseDocTask, 1, TimeUnit.DAYS);
            }
        }
//        获取Course信息
//        从redis中获取
        Course course = JSONObject.toJavaObject(this.redisCache.getCacheObject("Course:" + courseDocTask.getCourseID()), Course.class);
        if (Objects.isNull(course)) {
            course = this.courseDao.selectByCourseID(courseDocTask.getCourseID());
            if (Objects.isNull(course)) {
                jsonObject.put("msg", "找不到课程信息");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            } else {
//                将信息放入redis中
                this.redisCache.setCacheObject("Course:" + course.getCourseID(), course, 1, TimeUnit.DAYS);
            }
        }
//        根据CourseDocTask信息构建文件目录
        String fileDir = courseDocTask.getSchoolStartYear() + "-" + courseDocTask.getSchoolEndYear() + "-" + courseDocTask.getSchoolTerm() + File.separator + courseDocTask.getCourseID() + "_" + course.getCourseName() + File.separator;
        File tempFileDir = new File(prePath + File.separator + fileDir);
        if (!tempFileDir.exists()) {
            if (!tempFileDir.mkdirs()) {
                jsonObject.put("msg", "文件夹新建失败");
                return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
            }
        }
//        构建新的文件以及缓存文件
        File file = new File(tempFileDir.getAbsolutePath(), fileName);
        File tempFile = new File(this.tempPrePath, fileName);
        if (!tempFile.exists()) {
            jsonObject.put("msg", "文件不存在，无法提交");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        }
//        复制文件
        try (InputStream inputStream = new FileInputStream(tempFile); OutputStream outputStream = new FileOutputStream(file)) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, length);
            }
        } catch (FileNotFoundException e) {
            jsonObject.put("msg", "文件无法找到");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        } catch (IOException e) {
            jsonObject.put("msg", "IO操作错误");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        }
        CourseDocDetail courseDocDetail = new CourseDocDetail();
        courseDocDetail.setTaskID(courseDocTask.getID());
        courseDocDetail.setDocPath(fileDir + fileName);
        courseDocDetail.setUploadTime(new Date());
        courseDocDetail.setSubmitter("test");
        courseDocDetail.setDocTypeID(1);
//        Long num = courseDocDetailMapper.insertCourseDocDetail(courseDocDetail);
//        TODO 上传数据库
        return new ResponseResult(ResultCode.SUCCESS, courseDocDetail);
    }
}
