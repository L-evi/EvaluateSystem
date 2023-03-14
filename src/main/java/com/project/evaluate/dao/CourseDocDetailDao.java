package com.project.evaluate.dao;

import com.project.evaluate.entity.CourseDocDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface CourseDocDetailDao {

    //    根据taskID删除
    Long deleteByTaskID(int taskID, String userID);

    //    根据taskID查询
    CourseDocDetail selectByTaskID(Integer taskID, String userID);

    //    根据taskID查询单个数据
    CourseDocDetail selectOneByTaskID(Integer taskID);

    //    根据taskID和submitter查询数据
    CourseDocDetail selectByTaskIDAndSubmitter(Integer taskID, String submitter);

    List<CourseDocDetail> getAll();

    //    插入数据
    Long insertCourseDocDetail(CourseDocDetail courseDocDetail);

    //   根据ID删除
    Long deleteByID(int ID, String userID);

    // 根据ID更新
    Long updateByID(CourseDocDetail courseDocDetail);

    //    根据ID查询
    CourseDocDetail selectByID(int ID);
}
