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
import com.project.evaluate.entity.Course;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.service.CourseDocTaskService;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.events.Event;

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
    public ResponseResult exportTeachingDocuments(List<Integer> ids) {
        JSONObject jsonObject = new JSONObject();
        List<CourseDocTask> courseDocTasks = courseDocTaskDao.selectPageID(ids);
        if (Objects.isNull(courseDocTasks) || courseDocTasks.isEmpty()) {
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        导出为excel
        String filename = this.tempPrePath + File.separator + System.currentTimeMillis() + ".xlsx";
        EasyExcel.write(filename, CourseDocTask.class).sheet("教学文档任务清单").doWrite(courseDocTasks);
        File file = new File(filename);
        if (file.exists()) {
            jsonObject.put("msg", "导出成功");
            jsonObject.put("filename", filename);
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        }
        jsonObject.put("msg", "导出失败");
        return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
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
