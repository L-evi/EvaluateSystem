package com.project.evaluate.dao;

import com.project.evaluate.entity.DO.CourseDocDetailDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CourseDocDetailDao {

    //    根据taskID删除
    Long deleteByTaskID(int taskID, String userID);

    //    根据taskID查询
    List<CourseDocDetailDO> selectByTaskID(Integer taskID);

    //    根据taskID查询单个数据
    CourseDocDetailDO selectOneByTaskID(Integer taskID);

    List<CourseDocDetailDO> getAll();

    //    插入数据
    Long insertCourseDocDetail(CourseDocDetailDO courseDocDetailDO);

    //   根据ID删除
    Long deleteByID(int ID, String userID);

    //    根据ID查询
    CourseDocDetailDO selectByID(int ID);
}
