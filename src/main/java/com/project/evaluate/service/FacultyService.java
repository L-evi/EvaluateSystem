package com.project.evaluate.service;

import com.project.evaluate.entity.Faculty;
import com.project.evaluate.util.response.ResponseResult;

public interface FacultyService {
    ResponseResult userLogin(Faculty faculty);

    ResponseResult userRegister(Faculty faculty);

    ResponseResult insertFaculty(Faculty faculty);

    ResponseResult importFaculty(String filename);

    ResponseResult updateFaculty(Faculty faculty);

    ResponseResult resetFaculty(String userID);

    ResponseResult selectPageFaculty(Faculty faculty, Integer page, Integer pageSize, String orderBy);

    ResponseResult deleteFaculty(String userID);

    ResponseResult resetPassword(String userID, String oldPassword, String password);
}
