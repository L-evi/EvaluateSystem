package com.project.evaluate.mapper;

import com.project.evaluate.entity.CourseDocTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CourseDocTaskMapper {
    List<CourseDocTask> screenTeacherCourseDocTask(Map<String, Object> map);

}
