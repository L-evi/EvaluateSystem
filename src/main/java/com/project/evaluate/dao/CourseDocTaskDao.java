package com.project.evaluate.dao;

import com.project.evaluate.entity.DO.CourseDocTaskDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CourseDocTaskDao {

    List<Map<String, Object>> selectPageCourseDocTask(CourseDocTaskDO courseDocTaskDO);

    Long deleteTaskByID(int ID);

    CourseDocTaskDO selectByID(int ID);

    List<CourseDocTaskDO> selectPageID(@Param("ids") List<Integer> ids);

    Boolean updateCourseDocTask(CourseDocTaskDO courseDocTaskDO);

    Boolean updateCourseDocTaskStatus(Integer ID,Integer status);
}
