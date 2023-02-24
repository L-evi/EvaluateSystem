package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.DO.FeedbackDO;
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
    @DataLog(modelName = "提交反馈意见", operationType = "insert")
    public ResponseResult insertFeedback(@RequestBody FeedbackDO feedbackDO, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(feedbackDO)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String token = request.getHeader("token");
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            String userID = (String) jsonObject.get("userID");
            feedbackDO.setUserID(userID);
            feedbackDO.setFeedBackTime(new Date());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.feedbackService.insertFeedback(feedbackDO);
    }

    @GetMapping("/get/single")
    @DataLog(modelName = "查看反馈意见详情", operationType = "select")
    public ResponseResult getSingleFeedback(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.feedbackService.selectSingleFeedback(ID);
    }

    @GetMapping("/get/page")
    @DataLog(modelName = "分页查看反馈意见", operationType = "select")
    public ResponseResult getPageFeedback(Integer page, Integer pageSize, @DefaultValue("ID ASC") String orderBy, FeedbackDO feedbackDO) {
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        return this.feedbackService.selectFeedbacks(feedbackDO, page, pageSize, orderBy);
    }

    @RequiresRoles(value = "1", logical = Logical.OR)
    @DeleteMapping("/delete/single")
    @DataLog(modelName = "删除反馈意见", operationType = "delete")
    public ResponseResult deleteFeedback(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.feedbackService.deleteFeedback(ID);
    }
}
