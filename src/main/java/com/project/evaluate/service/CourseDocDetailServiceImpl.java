package com.project.evaluate.service;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.mapper.CourseDocDetailMapper;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:12
 */
@Service
public class CourseDocDetailServiceImpl implements CourseDocDetailService {

    @Resource
    private CourseDocDetailMapper courseDocDetailMapper;

    @Override
    public ResponseResult deleteByTaskID(int taskID) {
        Long num = this.courseDocDetailMapper.deleteByTaskID(taskID);
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
}
