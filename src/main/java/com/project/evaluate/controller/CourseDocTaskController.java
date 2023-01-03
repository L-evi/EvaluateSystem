package com.project.evaluate.controller;

import com.project.evaluate.service.CourseDocTaskService;
import com.project.evaluate.util.response.ResponseResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:08
 */
@RestController
@RequestMapping(value = "/api/courseDocTask/")
public class CourseDocTaskController {

    @Resource
    private CourseDocTaskService courseDocTaskService;

    @RequestMapping(value = "/search")
    public ResponseResult searchTeachingDocuments(@RequestBody Map<String, Object> map) {

        return this.courseDocTaskService.searchTeachingDocuments(map);
    }

}
