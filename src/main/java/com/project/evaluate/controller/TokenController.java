package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.service.TokenService;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/20 01:00
 */
@RestController
@RequestMapping(value = "/api")
@CrossOrigin("*")
public class TokenController {

    @Resource
    private TokenService tokenService;


    @RequestMapping(value = "/token/getMessage", method = RequestMethod.GET)
    public ResponseResult getTokenMessage(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (Strings.hasText(token)) {
            return this.tokenService.getTokenMessage(token);
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "token获取失败");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
    }
}
