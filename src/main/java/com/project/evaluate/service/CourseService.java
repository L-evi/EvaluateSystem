package com.project.evaluate.service;

import com.project.evaluate.entity.DO.CourseDO;
import com.project.evaluate.util.response.ResponseResult;

public interface CourseService {
    ResponseResult selectPageCourse(CourseDO courseDO, Integer page, Integer pageSize, String orderBy);

    ResponseResult importExcelCourse(String filename);
}
