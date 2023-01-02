package com.project.evaluate.handler;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description shiro的全局处理异常类
 * @since 2023/1/2 15:25
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnknownAccountException.class)
    public ResponseResult UnknownAccountExceptionHandler(UnknownAccountException e) {
        System.out.println("登录账号错误");
        e.printStackTrace();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "登录账号不存在，请重试");
        jsonObject.put("error", e.getMessage());
        return new ResponseResult(ResultCode.LOGIN_ERROR, jsonObject);
    }

    @ExceptionHandler(IncorrectCredentialsException.class)
    public ResponseResult IncorrectCredentialsExceptionHandler(IncorrectCredentialsException e) {
        System.out.println("登录密码错误");
        e.printStackTrace();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "登录密码错误，请重试");
        jsonObject.put("error", e.getMessage());
        return new ResponseResult(ResultCode.LOGIN_ERROR, jsonObject);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseResult AuthenticationExceptionHandler(AuthenticationException e) {
        System.out.println("Token 错误");
        e.printStackTrace();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "token 错误");
        jsonObject.put("error", e.getMessage());
        return new ResponseResult(ResultCode.TOKEN_EXPIRATION, jsonObject);
    }
}
