package com.project.evaluate.dao;

import com.project.evaluate.entity.Faculty;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FacultyDao {
    Faculty selectByUserID(String userID);

    int addFaculty(Faculty faculty);

    int updateFaculty(Faculty faculty);
}
