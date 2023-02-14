package com.project.evaluate.service;

import com.project.evaluate.entity.Course;
import com.project.evaluate.util.response.ResponseResult;

public interface CourseService {
    ResponseResult selectPageCourse(Course course, Integer page, Integer pageSize, String orderBy);
}
