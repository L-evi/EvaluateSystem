package com.project.evaluate.dao;

import com.project.evaluate.entity.CourseDocDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CourseDocDetailDao {

    //    根据taskID删除
    Long deleteByTaskID(int taskID, String userID);

    //    根据taskID查询
    List<CourseDocDetail> selectByTaskID(Integer taskID);

    //    根据taskID查询单个数据
    CourseDocDetail selectOneByTaskID(Integer taskID);

    List<CourseDocDetail> getAll();

    //    插入数据
    Long insertCourseDocDetail(CourseDocDetail courseDocDetail);

    //   根据ID删除
    Long deleteByID(int ID, String userID);

    //    根据ID查询
    CourseDocDetail selectByID(int ID);
}
