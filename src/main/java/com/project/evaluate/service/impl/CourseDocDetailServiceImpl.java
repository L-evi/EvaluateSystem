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
import com.project.evaluate.service.CourseDocDetailService;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:12
 */
@Service
public class CourseDocDetailServiceImpl implements CourseDocDetailService {

    @Resource
    private CourseDocDetailDao courseDocDetailDao;

    @Resource
    private RedisCache redisCache;

    @Resource
    private CourseDao courseDao;

    @Resource
    private CourseDocTaskDao courseDocTaskDao;

    @Value("${file.temp-pre-path}")
    private String tempPrePath;

    @Override
    public ResponseResult deleteByTaskID(Integer taskID, String userID) {
        Long num = this.courseDocDetailDao.deleteByTaskID(taskID, userID);
        JSONObject jsonObject = new JSONObject();
        if (num > 0) {
            jsonObject.put("msg", "删除成功");
            jsonObject.put("count", num);
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        }
        if (num == 0) {
            jsonObject.put("msg", "删除失败，暂无数据可删除");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        return new ResponseResult(ResultCode.DATABASE_ERROR);
    }

    @Override
    public ResponseResult deleteByID(Integer ID, Integer roleType, String userID) {
        JSONObject jsonObject = new JSONObject();
        CourseDocDetail courseDocDetail = this.courseDocDetailDao.selectByID(ID);
        CourseDocTask courseDocTask = courseDocTaskDao.selectByID(courseDocDetail.getTaskID());
        if (!Objects.isNull(courseDocTask) && (courseDocTask.getCloseTask() == 1 || courseDocTask.getDeadline().before(new Date()))) {
            jsonObject.put("msg", "删除失败，任务已关闭或已过期");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        if (Objects.isNull(courseDocDetail)) {
            jsonObject.put("msg", "删除失败，暂无数据可删除");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        Long num = -1L;
        if (roleType.intValue() == 2) {
            num = courseDocDetailDao.deleteByID(ID, null);
        }
        if (roleType.intValue() == 0) {
            num = courseDocDetailDao.deleteByID(ID, userID);
        }
        if (num > 0) {
            jsonObject.put("msg", "删除成功");
            jsonObject.put("num", num);
            deleteFile(courseDocDetail.getDocPath());
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        }
        jsonObject.put("msg", "删除失败，暂无数据可删除");
        return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
    }

    private boolean deleteFile(String filename) {
        if (!Strings.hasText(filename)) {
            return false;
        }
        File file = new File(filename);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    @Override
    public ResponseResult selectByTaskID(Integer taskID, Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(page, pageSize, orderBy);
        List<CourseDocDetail> courseDocDetails = this.courseDocDetailDao.selectByTaskID(taskID);
        if (Objects.isNull(courseDocDetails) || courseDocDetails.isEmpty()) {
            jsonObject.put("msg", "暂无数据");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<CourseDocDetail> pageInfo = new PageInfo<>(courseDocDetails);
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(pageInfo.getList()));
        jsonObject.put("msg", "查询成功");
        jsonObject.put("total", pageInfo.getTotal());
        jsonObject.put("array", jsonArray);
        jsonObject.put("pages", pageInfo.getPages());
        return new ResponseResult<>(ResultCode.SUCCESS, jsonObject);
    }

    @Value("${file.teach-root-path}")
    private String teachRootPath;

    @Override
    public ResponseResult submitDocument(CourseDocDetail courseDocDetail) {
        JSONObject jsonObject = new JSONObject();
//        获取CourseDocTask信息
//        从redis中获取CourseDocTask信息
        CourseDocTask courseDocTask = JSONObject.toJavaObject(this.redisCache.getCacheObject("CourseDocTask:" + courseDocDetail.getTaskID()), CourseDocTask.class);
        if (Objects.isNull(courseDocTask)) {
            courseDocTask = this.courseDocTaskDao.selectByID(courseDocDetail.getTaskID());
            if (Objects.isNull(courseDocTask)) {
                jsonObject.put("msg", "找不到课程文档任务信息");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            } else {
//                将信息放入redis中
                this.redisCache.setCacheObject("CourseDocTask:" + courseDocDetail.getTaskID(), courseDocTask, 1, TimeUnit.DAYS);
            }
        }
//        获取Course信息
//        从redis中获取
        Course course = JSONObject.toJavaObject(this.redisCache.getCacheObject("Course:" + courseDocTask.getCourseID()), Course.class);
        if (Objects.isNull(course)) {
            // todo: 修改course为List
            course = this.courseDao.selectByCourseID(courseDocTask.getCourseID()).get(0);
            if (Objects.isNull(course)) {
                jsonObject.put("msg", "找不到课程信息");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            } else {
//                将信息放入redis中
                this.redisCache.setCacheObject("Course:" + course.getCourseID(), course, 1, TimeUnit.DAYS);
            }
        }
//        根据CourseDocTask信息构建文件目录
        String fileDir = teachRootPath + File.separator
                + courseDocTask.getSchoolStartYear() + "-" + courseDocTask.getSchoolEndYear() + "(" + courseDocTask.getSchoolTerm() + ")" + File.separator
                + courseDocTask.getCourseID() + "-" + course.getCourseName() + File.separator;
        File dir = new File(fileDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                jsonObject.put("msg", "文件夹新建失败");
                return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
            }
        }
//        构建新的文件以及缓存文件
        File tempFile = new File(courseDocDetail.getDocPath());
        if (!tempFile.exists()) {
            jsonObject.put("msg", "文件不存在，无法提交");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        }
        File file = new File(fileDir, tempFile.getName());
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
        courseDocDetail.setDocPath(file.getAbsolutePath());
        Long num = courseDocDetailDao.insertCourseDocDetail(courseDocDetail);
        jsonObject = JSONObject.parseObject(JSON.toJSONString(courseDocDetail));
        jsonObject.put("msg", "提交成功");
        jsonObject.put("count", "num");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

}
