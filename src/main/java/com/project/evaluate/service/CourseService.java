package com.project.evaluate.service;

import com.project.evaluate.entity.Course;
import com.project.evaluate.util.response.ResponseResult;

public interface CourseService {
    ResponseResult selectPageCourse(Course course, Integer page, Integer pageSize, String orderBy);

    ResponseResult selectCourseByID(Integer ID);

    ResponseResult selectCourseByCourseID(Integer page, Integer pageSize,String courseID);

    ResponseResult insertCourse(Course course);

    ResponseResult updateCourse(Course course);

    ResponseResult deleteCourse(Integer ID, String courseID);

    ResponseResult importExcelCourse(String filename);
}
