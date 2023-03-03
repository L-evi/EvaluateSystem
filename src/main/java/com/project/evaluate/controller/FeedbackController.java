package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.Feedback;
import com.project.evaluate.service.FeedbackService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/9 21:30
 */

@RestController
@CrossOrigin("*")
@RequestMapping("/api/feedback")
public class FeedbackController {
    @Resource
    private FeedbackService feedbackService;

    @PostMapping("/add")
    public ResponseResult insertFeedback(@RequestBody Feedback feedback, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(feedback)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String token = request.getHeader("token");
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            String userID = (String) jsonObject.get("userID");
            feedback.setUserID(userID);
            feedback.setFeedBackTime(new Date());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.feedbackService.insertFeedback(feedback);
    }

    @GetMapping("/get/single")
    public ResponseResult getSingleFeedback(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.feedbackService.selectSingleFeedback(ID);
    }

    @GetMapping("/get/page")
    public ResponseResult getPageFeedback(Integer page, Integer pageSize, @DefaultValue("ID ASC") String orderBy, Feedback feedback) {
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        return this.feedbackService.selectFeedbacks(feedback, page, pageSize, orderBy);
    }

    @RequiresRoles(value = "1", logical = Logical.OR)
    @DeleteMapping("/delete/single")
    public ResponseResult deleteFeedback(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.feedbackService.deleteFeedback(ID);
    }
}
