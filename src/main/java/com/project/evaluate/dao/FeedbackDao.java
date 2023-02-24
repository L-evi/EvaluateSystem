package com.project.evaluate.dao;


import com.project.evaluate.entity.DO.FeedbackDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FeedbackDao {
    FeedbackDO selectByID(Integer ID);

    List<FeedbackDO> selectByFeedback(FeedbackDO feedbackDO);

    Long insert(FeedbackDO feedbackDO);

    Boolean delete(Integer ID);
}
