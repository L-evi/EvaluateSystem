package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.DocShare;
import com.project.evaluate.service.DocShareService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/14 10:05
 */
@RestController
@RequestMapping(value = "/api/doc-share")
public class DocShareController {
    @Resource
    private DocShareService docShareService;

    @PostMapping(value = "/add")
    public ResponseResult insertDocShare(@RequestBody DocShare docShare, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (!Strings.hasText(docShare.getSubmitter())) {
            try {
                String token = request.getHeader("token");
                jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
                String userID = (String) jsonObject.get("userID");
                docShare.setSubmitter(userID);
            } catch (Exception e) {

            }
            jsonObject.clear();
        }
        if (Objects.isNull(docShare)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        if (Objects.isNull(docShare.getUploadTime())) {
            docShare.setUploadTime(new Date());
        }
        return this.docShareService.addDocShare(docShare);
    }

    @GetMapping(value = "/get/single")
    public ResponseResult selectSingleDocShare(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.docShareService.selectDocShareByID(ID);
    }

    @GetMapping(value = "/get/page")
    public ResponseResult selectPageDocShare(Integer page, Integer pageSize, String orderBy, DocShare docShare) {
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        if (!Strings.hasText(orderBy)) {
            orderBy = "uploadTime DESC";
        }
        if (Objects.isNull(docShare)) {
            docShare = new DocShare();
        }
        return this.docShareService.selectPageDocShare(docShare, page, pageSize, orderBy);
    }

    @PutMapping(value = "/update")
    public ResponseResult updateDocShare(@RequestBody DocShare docShare, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(docShare)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String token = request.getHeader("token");
        return this.docShareService.updateDocShare(docShare, token);
    }

    @DeleteMapping(value = "/delete")
    public ResponseResult deleteDocShare(Integer id, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(id) || id == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String token = request.getHeader("token");
        return this.docShareService.deleteDocShare(id, token);
    }

}
