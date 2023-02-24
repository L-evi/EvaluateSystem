package com.project.evaluate.service;

import com.project.evaluate.entity.DO.CourseDocTaskDO;
import com.project.evaluate.util.response.ResponseResult;

import java.util.List;

public interface CourseDocTaskService {

    ResponseResult selectPageCourseDocTask(CourseDocTaskDO courseDocTaskDO, Integer page, Integer pageSize, String orderBy);

    ResponseResult deleteTeachingDocuments(Integer ID);

    ResponseResult exportTeachingDocuments(List<Integer> ids);

    ResponseResult updateCourseDocTask(CourseDocTaskDO courseDocTaskDO);

    ResponseResult resetCourseDocTask(Integer ID, Integer status);

    ResponseResult insertCourseDocTask(CourseDocTaskDO courseDocTaskDO);
}
