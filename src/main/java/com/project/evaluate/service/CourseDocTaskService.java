package com.project.evaluate.service;

import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.util.response.ResponseResult;

import java.util.List;
import java.util.Map;

public interface CourseDocTaskService {

    ResponseResult selectPageCourseDocTask(CourseDocTask courseDocTask, Integer page, Integer pageSize, String orderBy);

    ResponseResult deleteTeachingDocuments(int ID);

    ResponseResult exportTeachingDocuments(List<Integer> ids);

    ResponseResult updateCourseDocTask(CourseDocTask courseDocTask);

    ResponseResult submitDocument(Map<String, Object> map);
}
