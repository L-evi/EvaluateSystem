package com.project.evaluate.mapper;

import com.project.evaluate.entity.CourseDocDetail;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CourseDocDetailMapper {

    //    根据taskID删除
    Long deleteByTaskID(int taskID, String userID);

    //    根据taskID查询
    List<CourseDocDetail> selectByTaskID(Map<String, Object> map);
}
