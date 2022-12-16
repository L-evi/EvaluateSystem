package com.project.evaluate.controller.File;

import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/16 15:22
 */
@RequestMapping("/api/common")
@Controller
@CrossOrigin("*")
class UploadController {
    @Value("${file.pre-path}")
    private static String utf8;


    @RequestMapping(value = "/upload")
    public ResponseResult upload(HttpServletResponse response, HttpServletRequest request) {

        return new ResponseResult(ResultCode.SUCCESS);
    }

}
