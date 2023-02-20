package com.project.evaluate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.CourseDao;
import com.project.evaluate.dao.CourseDocDetailDao;
import com.project.evaluate.dao.CourseDocTaskDao;
import com.project.evaluate.entity.DO.CourseDO;
import com.project.evaluate.entity.DO.CourseDocDetailDO;
import com.project.evaluate.entity.DO.CourseDocTaskDO;
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
import java.util.Map;
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
        CourseDocDetailDO courseDocDetailDO = this.courseDocDetailDao.selectByID(ID);
        CourseDocTaskDO courseDocTaskDO = courseDocTaskDao.selectByID(courseDocDetailDO.getTaskID());
        if (!Objects.isNull(courseDocTaskDO) && (courseDocTaskDO.getCloseTask() == 1 || courseDocTaskDO.getDeadline().before(new Date()))) {
            jsonObject.put("msg", "删除失败，任务已关闭或已过期");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        if (Objects.isNull(courseDocDetailDO)) {
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
            deleteFile(courseDocDetailDO.getDocPath());
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
        List<CourseDocDetailDO> courseDocDetailDOS = this.courseDocDetailDao.selectByTaskID(taskID);
        if (Objects.isNull(courseDocDetailDOS) || courseDocDetailDOS.isEmpty()) {
            jsonObject.put("msg", "暂无数据");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<CourseDocDetailDO> pageInfo = new PageInfo<>(courseDocDetailDOS);
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(pageInfo.getList()));
        jsonObject.put("msg", "查询成功");
        jsonObject.put("total", pageInfo.getTotal());
        jsonObject.put("array", jsonArray);
        jsonObject.put("pages", pageInfo.getPages());
        return new ResponseResult<>(ResultCode.SUCCESS, jsonObject);
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
        CourseDocTaskDO courseDocTaskDO = JSONObject.toJavaObject(this.redisCache.getCacheObject("CourseDocTaskDO:" + taskID), CourseDocTaskDO.class);
        if (Objects.isNull(courseDocTaskDO)) {
            courseDocTaskDO = this.courseDocTaskDao.selectByID(taskID);
            if (Objects.isNull(courseDocTaskDO)) {
                jsonObject.put("msg", "找不到课程文档任务信息");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            } else {
//                将信息放入redis中
                this.redisCache.setCacheObject("CourseDocTaskDO:" + taskID, courseDocTaskDO, 1, TimeUnit.DAYS);
            }
        }
//        获取Course信息
//        从redis中获取
        CourseDO courseDO = JSONObject.toJavaObject(this.redisCache.getCacheObject("CourseDO:" + courseDocTaskDO.getCourseID()), CourseDO.class);
        if (Objects.isNull(courseDO)) {
            courseDO = this.courseDao.selectByCourseID(courseDocTaskDO.getCourseID());
            if (Objects.isNull(courseDO)) {
                jsonObject.put("msg", "找不到课程信息");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            } else {
//                将信息放入redis中
                this.redisCache.setCacheObject("CourseDO:" + courseDO.getCourseID(), courseDO, 1, TimeUnit.DAYS);
            }
        }
//        根据CourseDocTask信息构建文件目录
        String fileDir = courseDocTaskDO.getSchoolStartYear() + "-" + courseDocTaskDO.getSchoolEndYear() + "-" + courseDocTaskDO.getSchoolTerm() + File.separator + courseDocTaskDO.getCourseID() + "_" + courseDO.getCourseName() + File.separator;
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
        CourseDocDetailDO courseDocDetailDO = new CourseDocDetailDO();
        courseDocDetailDO.setTaskID(courseDocTaskDO.getID());
        courseDocDetailDO.setDocPath(fileDir + fileName);
        courseDocDetailDO.setUploadTime(new Date());
        courseDocDetailDO.setSubmitter("test");
        courseDocDetailDO.setDocTypeID(1);
        Long num = courseDocDetailDao.insertCourseDocDetail(courseDocDetailDO);
        jsonObject = JSONObject.parseObject(JSON.toJSONString(courseDocDetailDO));
        jsonObject.put("msg", "提交成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

}
