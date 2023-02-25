package com.project.evaluate.dao;

import com.project.evaluate.entity.Faculty;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FacultyDao {
    Faculty selectByUserID(String userID);

    Integer insertFaculty(Faculty faculty);

    Integer updateFaculty(Faculty faculty);

    Integer resetFaculty(String userID, String password);

    List<Faculty> selectPageFaculty(Faculty faculty);

    Integer deletePageFaculty(String userID);

    Integer banFaculty(String userID);

    Integer resetPassword(String userID, String password, String oldPassword);
}
