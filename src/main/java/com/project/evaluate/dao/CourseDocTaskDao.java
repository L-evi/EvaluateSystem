package com.project.evaluate.dao;

import com.project.evaluate.entity.CourseDocTask;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CourseDocTaskDao {

    List<Map<String, Object>> selectPageCourseDocTask(CourseDocTask courseDocTask);

    Long deleteTaskByID(int ID);

    CourseDocTask selectByID(int ID);

    List<CourseDocTask> selectPageID(@Param("ids") List<Integer> ids);

    Boolean updateCourseDocTask(CourseDocTask courseDocTask);
}
