package com.project.evaluate.dao;


import com.project.evaluate.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CourseDao {
    List<Course> selectPageCourse(Course course);

    Integer countCourse();

    Course selectByCourseID(String courseID);

    Integer insertCourse(Course course);

    Integer insertPageCourse(@Param("courses") List<Course> courses);

    Boolean deletaByID(Integer ID);
}
