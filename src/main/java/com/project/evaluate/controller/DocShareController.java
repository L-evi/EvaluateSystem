package com.project.evaluate.controller;

import com.project.evaluate.service.DocShareService;
import com.project.evaluate.util.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/14 10:05
 */
@RestController
@RequestMapping(value = "/api/doc-share/")
public class DocShareController {
    @Resource
    private DocShareService docShareService;

    @PostMapping(value = "/add")
    public ResponseResult insertDocShare() {
        return null;
    }

    @GetMapping(value = "/get/single")
    public ResponseResult selectSingleDocShare() {
        return null;
    }

    @GetMapping(value = "/get/page")
    public ResponseResult selectPageDocShare() {
        return null;
    }

    @PutMapping(value = "/update")
    public ResponseResult updateDocShare() {
        return null;
    }

    @DeleteMapping(value = "/delete")
    public ResponseResult deleteDocShare() {
        return null;
    }

}
