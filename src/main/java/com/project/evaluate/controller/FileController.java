package com.project.evaluate.controller;

import com.project.evaluate.entity.file.FileChunkDto;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 17:42
 */
@RestController
@RequestMapping(value = "/file")
public class FileController {

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public ResponseResult checkUpload(FileChunkDto fileChunkDto) {
        return new ResponseResult(ResultCode.SUCCESS);
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseResult upload(FileChunkDto fileChunkDto, HttpServletResponse response) {

        return new ResponseResult(ResultCode.SUCCESS);
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void download(HttpServletRequest request, HttpServletResponse response, @PathVariable("identifier") String identifier) {

    }
}
