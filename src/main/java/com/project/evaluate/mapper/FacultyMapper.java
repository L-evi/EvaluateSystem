package com.project.evaluate.mapper;

import com.project.evaluate.entity.Faculty;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface FacultyMapper {
    Faculty selectByUsername(String username);

    int addFaculty(Faculty faculty);
}
