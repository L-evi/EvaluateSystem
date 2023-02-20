package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.DO.DocShareDO;
import com.project.evaluate.service.DocShareService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
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
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    @DataLog(modelName = "上传共享文件资料", operationType = "insert")
    public ResponseResult insertDocShare(@RequestBody DocShareDO docShareDO, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (!Strings.hasText(docShareDO.getSubmitter())) {
            try {
                String token = request.getHeader("token");
                jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
                String userID = (String) jsonObject.get("userID");
                docShareDO.setSubmitter(userID);
            } catch (Exception e) {

            }
            jsonObject.clear();
        }
        if (Objects.isNull(docShareDO)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        if (Objects.isNull(docShareDO.getUploadTime())) {
            docShareDO.setUploadTime(new Date());
        }
        return this.docShareService.addDocShare(docShareDO);
    }

    @DataLog(modelName = "查看共享文件详情", operationType = "select")
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
    @DataLog(modelName = "分页查看共享文件", operationType = "select")
    public ResponseResult selectPageDocShare(Integer page, Integer pageSize, String orderBy, DocShareDO docShareDO) {
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        if (!Strings.hasText(orderBy)) {
            orderBy = "uploadTime DESC";
        }
        if (Objects.isNull(docShareDO)) {
            docShareDO = new DocShareDO();
        }
        return this.docShareService.selectPageDocShare(docShareDO, page, pageSize, orderBy);
    }

    @PutMapping(value = "/update")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    @DataLog(modelName = "修改共享文件资料", operationType = "update")
    public ResponseResult updateDocShare(@RequestBody DocShareDO docShareDO, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(docShareDO)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String token = request.getHeader("token");
        return this.docShareService.updateDocShare(docShareDO, token);
    }

    @DeleteMapping(value = "/delete")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    @DataLog(modelName = "删除共享文件资料", operationType = "delete")
    public ResponseResult deleteDocShare(Integer id, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(id) || id == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String token = request.getHeader("token");
        return this.docShareService.deleteDocShare(id, token);
    }

    @PostMapping("/submit")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
//    TODO 共享文件：提交上传的文档是否需要记录日志
    public ResponseResult submitDocShare(@RequestBody DocShareDO docShareDO) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(docShareDO)
                || Objects.isNull(docShareDO.getDocPath())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.docShareService.submitDocument(docShareDO);
    }
}
