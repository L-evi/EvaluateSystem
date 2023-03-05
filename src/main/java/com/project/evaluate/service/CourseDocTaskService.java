package com.project.evaluate.service;

import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.util.response.ResponseResult;

import java.util.List;

public interface CourseDocTaskService {

    ResponseResult selectPageCourseDocTask(CourseDocTask courseDocTask, Integer page, Integer pageSize, String orderBy);

    ResponseResult deleteTeachingDocuments(Integer ID);

    ResponseResult exportCourseDocTask(CourseDocTask courseDocTask);

    ResponseResult updateCourseDocTask(CourseDocTask courseDocTask);

    ResponseResult resetCourseDocTask(Integer ID, Integer status);

    ResponseResult insertCourseDocTask(List<CourseDocTask> courseDocTasks);
}
