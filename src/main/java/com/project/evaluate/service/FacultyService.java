package com.project.evaluate.service;

import com.project.evaluate.entity.DO.FacultyDO;
import com.project.evaluate.util.response.ResponseResult;

public interface FacultyService {
    ResponseResult userLogin(FacultyDO facultyDO);

    ResponseResult userRegister(FacultyDO facultyDO);

    ResponseResult insertFaculty(FacultyDO facultyDO);

    ResponseResult updateFaculty(FacultyDO facultyDO);

    ResponseResult resetFaculty(String userID);

    ResponseResult selectPageFaculty(FacultyDO facultyDO, Integer page, Integer pageSize, String orderBy);

    ResponseResult deleteFaculty(String userID);

    ResponseResult resetPassword(String userID, String oldPassword, String password);
}
