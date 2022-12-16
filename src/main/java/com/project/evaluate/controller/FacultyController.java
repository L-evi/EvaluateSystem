package com.project.evaluate.controller;

import com.project.evaluate.entity.Faculty;
import com.project.evaluate.service.FacultyService;
import com.project.evaluate.util.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 02:06
 */
@RestController
@RequestMapping(value = "/user")
@CrossOrigin(value = "*")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseResult userLogin(@RequestBody Map<String, Object> dataMap) {
//        获取其中的数据
        Faculty faculty = new Faculty();
        faculty.setUserID((String) dataMap.get("userID"));
        faculty.setPassword((String) dataMap.get("password"));
        return facultyService.userLogin(faculty);
    }
}
