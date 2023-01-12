package com.project.evaluate.service;

import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.util.response.ResponseResult;

public interface CourseDocTaskService {

    ResponseResult selectPageCourseDocTask(CourseDocTask courseDocTask, Integer page, Integer pageSize, String orderBy);

    ResponseResult deleteTeachingDocuments(int ID);
}
