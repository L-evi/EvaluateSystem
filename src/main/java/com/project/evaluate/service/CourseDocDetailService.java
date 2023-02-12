package com.project.evaluate.service;

import com.project.evaluate.util.response.ResponseResult;

import java.util.Map;

public interface CourseDocDetailService {

    ResponseResult deleteByTaskID(Integer taskID, String userID);

    ResponseResult deleteByID(Integer ID, Integer roleType, String userID);

    ResponseResult selectByTaskID(Integer taskID, Integer page, Integer pageSize, String orderBy);

    ResponseResult submitDocument(Map<String, Object> map);
}
