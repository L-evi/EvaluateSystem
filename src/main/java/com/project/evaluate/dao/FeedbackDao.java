package com.project.evaluate.dao;


import com.project.evaluate.entity.Feedback;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FeedbackDao {
    Feedback selectByID(Integer id);

    List<Feedback> selectByFeedback(Feedback feedback);

    Long insert(Feedback feedback);

    Boolean delete(Integer id);
}
