package com.project.evaluate.dao;

import com.project.evaluate.entity.CourseDocTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CourseDocTaskDao {

    List<Map<String, Object>> selectPageCourseDocTask(CourseDocTask courseDocTask);

    Long deleteTaskByID(int ID);

    CourseDocTask selectByID(int ID);
}
