package com.project.evaluate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.mapper.CourseDocDetailMapper;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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
    public ResponseResult deleteByTaskID(int taskID, String userID) {
        Long num = this.courseDocDetailMapper.deleteByTaskID(taskID, userID);
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
    public ResponseResult selectByTaskID(Map<String, Object> map) {
        int page = 1;
        if (map.containsKey("page")) {
            page = Integer.valueOf((String) map.get("page"));
        }
        int pageSize = 10;
        if (map.containsKey("pageSize")) {
            pageSize = Integer.valueOf((String) map.get("pageSize"));
        }
        map.put("page", (page - 1) * pageSize);
        map.put("pageSize", pageSize);
        List<CourseDocDetail> courseDocDetails = this.courseDocDetailMapper.selectByTaskID(map);
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(courseDocDetails));
        return new ResponseResult<>(ResultCode.SUCCESS, jsonArray);
    }


}
