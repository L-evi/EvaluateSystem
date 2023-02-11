package com.project.evaluate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.CourseDocDetailDao;
import com.project.evaluate.dao.CourseDocTaskDao;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.service.CourseDocDetailService;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private CourseDocTaskDao courseDocTaskDao;

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


}
