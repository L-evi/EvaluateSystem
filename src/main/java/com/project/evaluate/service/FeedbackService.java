package com.project.evaluate.service;

import com.project.evaluate.entity.DO.FeedbackDO;
import com.project.evaluate.util.response.ResponseResult;

public interface FeedbackService {
    ResponseResult insertFeedback(FeedbackDO feedbackDO);

    ResponseResult selectSingleFeedback(Integer ID);

    ResponseResult selectFeedbacks(FeedbackDO feedbackDO, Integer page, Integer pageSize, String orderBy);

    ResponseResult deleteFeedback(Integer ID);
}
