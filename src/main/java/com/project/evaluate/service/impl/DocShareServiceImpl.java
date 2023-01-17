package com.project.evaluate.service.impl;

import com.project.evaluate.dao.DocShareDao;
import com.project.evaluate.service.DocShareService;
import com.project.evaluate.util.response.ResponseResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/14 10:05
 */
@Service
public class DocShareServiceImpl implements DocShareService {
    @Resource
    private DocShareDao docShareDao;

    public static ResponseResult insertDocShare() {
        return null;
    }

    public static ResponseResult selectSingleDocShare() {
        return null;
    }

    public static ResponseResult selectPageDocShare() {
        return null;
    }

    public static ResponseResult updateDocShare() {
        return null;
    }

    public static ResponseResult deleteDocShare() {
        return null;
    }

}
