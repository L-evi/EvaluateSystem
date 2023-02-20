package com.project.evaluate.dao;


import com.project.evaluate.entity.DO.CourseDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CourseDao {

    List<CourseDO> selectPageCourse(CourseDO courseDO);

    Integer countCourse();

    CourseDO selectByCourseID(String courseID);

    Integer insertCourse(CourseDO courseDO);

    Boolean deletaByID(Integer ID);
}
