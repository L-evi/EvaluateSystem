package com.project.evaluate.service;

import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.util.response.ResponseResult;

import java.util.List;
import java.util.Map;

public interface CourseDocTaskService {

    ResponseResult selectPageCourseDocTask(CourseDocTask courseDocTask, Integer page, Integer pageSize, String orderBy);

    ResponseResult deleteTeachingDocuments(Integer ID);

    ResponseResult exportTeachingDocuments(List<Integer> ids);

    ResponseResult updateCourseDocTask(CourseDocTask courseDocTask);

    ResponseResult resetCourseDocTask(Integer ID, Integer status);

    ResponseResult insertCourseDocTask(CourseDocTask courseDocTask);
}
