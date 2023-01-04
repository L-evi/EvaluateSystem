package com.project.evaluate.service;

import com.project.evaluate.util.response.ResponseResult;

import java.util.Map;

public interface CourseDocTaskService {
    ResponseResult searchTeachingDocuments(Map<String, Object> map);

    ResponseResult deleteTeachingDocuments(int ID);
}
