package com.project.evaluate.dao;

import com.project.evaluate.entity.DO.FacultyDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FacultyDao {
    FacultyDO selectByUserID(String userID);

    Integer insertFaculty(FacultyDO facultyDO);

    Integer updateFaculty(FacultyDO facultyDO);

    Integer resetFaculty(String userID, String password);

    List<FacultyDO> selectPageFaculty(FacultyDO facultyDO);

    Integer deletePageFaculty(String userID);

    Integer banFaculty(String userID);

    Integer resetPassword(String userID, String password, String oldPassword);
}
