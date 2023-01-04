package com.project.evaluate.service;

import com.project.evaluate.util.response.ResponseResult;

import java.util.Map;

public interface CourseDocDetailService {

    ResponseResult deleteByTaskID(int taskID, String userID);

    ResponseResult selectByTaskID(Map<String, Object> map);
}
