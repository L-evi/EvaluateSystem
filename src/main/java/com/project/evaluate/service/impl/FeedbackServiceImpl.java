package com.project.evaluate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.FeedbackDao;
import com.project.evaluate.entity.Feedback;
import com.project.evaluate.service.FeedbackService;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/9 21:17
 */
@Service
public class FeedbackServiceImpl implements FeedbackService {
    @Resource
    private FeedbackDao feedbackDao;

    @Resource
    private RedisCache redisCache;

    @Override
    public ResponseResult insertFeedback(Feedback feedback) {
        JSONObject jsonObject = new JSONObject();
        Long num = this.feedbackDao.insert(feedback);
        if (num < 1) {
            jsonObject.put("msg", "添加数据失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        jsonObject.put("msg", "添加数据成功");
        jsonObject.put("num", num);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectSingleFeedback(Integer ID) {
        JSONObject jsonObject = new JSONObject();
//        从redis中查询
        Feedback feedback = JSONObject.toJavaObject(this.redisCache.getCacheObject("Feedback:" + ID), Feedback.class);
        if (Objects.isNull(feedback)) {
            feedback = this.feedbackDao.selectByID(ID);
            if (Objects.isNull(feedback)) {
                jsonObject.put("msg", "查询失败");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
            this.redisCache.setCacheObject("Feedback:" + feedback.getID(), feedback, 1, TimeUnit.DAYS);
        }
        jsonObject = JSONObject.parseObject(JSON.toJSONString(feedback));
        jsonObject.put("msg", "查询成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectFeedbacks(Feedback feedback, Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(page, pageSize, orderBy);
        List<Feedback> feedbacks = this.feedbackDao.selectByFeedback(feedback);
        if (feedbacks == null || feedbacks.isEmpty()) {
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<Feedback> pageInfo = new PageInfo<>(feedbacks);
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(pageInfo.getList()));
        jsonObject.put("array", jsonArray);
        jsonObject.put("total", pageInfo.getTotal());
        jsonObject.put("pages", pageInfo.getPages());
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult deleteFeedback(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        Boolean isDelete = this.feedbackDao.delete(ID);
        if (!isDelete) {
            jsonObject.put("msg", "删除失败，数据不存在");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        从redis中删除
        this.redisCache.deleteObject("Feedback:" + ID);
        jsonObject.put("msg", "删除成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }
}
