package com.project.evaluate.dao;

import com.project.evaluate.entity.Faculty;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FacultyDao {
    Faculty selectByUserID(String userID);

    int insertFaculty(Faculty faculty);

    int updateFaculty(Faculty faculty);

    int resetFaculty(String userID, String password);

    List<Faculty> selectPageFaculty(Faculty faculty);

    int deletePageFaculty(String userID);

    int banFaculty(String userID);
}
