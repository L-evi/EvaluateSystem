package com.project.evaluate.service;

import com.project.evaluate.entity.Faculty;
import com.project.evaluate.util.response.ResponseResult;

public interface FacultyService {
    ResponseResult userLogin(Faculty faculty);

    ResponseResult userRegister(Faculty faculty);
}
