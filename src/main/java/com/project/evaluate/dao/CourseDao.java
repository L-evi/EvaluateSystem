package com.project.evaluate.dao;


import com.project.evaluate.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CourseDao {

    List<Course> getPageCourse(int start, int end);

    Integer countCourse();

    Course selectByCourseID(String courseID);
}
