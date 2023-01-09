package com.project.evaluate.service;

import com.project.evaluate.entity.Feedback;
import com.project.evaluate.util.response.ResponseResult;

public interface FeedbackService {
    ResponseResult insertFeedback(Feedback feedback);

    ResponseResult selectSingleFeedback(Integer id);

    ResponseResult selectFeedbacks(Feedback feedback, Integer page, Integer pageSize, String orderBy);

    ResponseResult deleteFeedback(Integer id);
}
