package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.Config;
import com.project.evaluate.service.ConfigService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/2/3 21:14
 */
@RestController
@RequestMapping(value = "/api/config")
@CrossOrigin("*")
@RequiresRoles(value = "1",logical = Logical.OR)
public class ConfigController {

    @Resource
    private ConfigService configService;

    @PostMapping(value = "/add")
    public ResponseResult insertConfig(@RequestBody Config config) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(config)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return configService.insertConfig(config);
    }

    @GetMapping("/get/single/ID")
    public ResponseResult selectByID(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return configService.selectByID(ID);
    }

    @GetMapping("/get/single/userID")
    public ResponseResult selectByUserID(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        String token = request.getHeader("token");
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            String userID = (String) jsonObject.get("userID");
            return configService.selectByUserID(userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get/page")
    public ResponseResult selectPageConfig(Integer page, Integer pageSize, String orderBy) {
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        if (!Strings.hasText(orderBy)) {
            orderBy = "ID ASC";
        }
        return configService.selectPageConfig(page, pageSize, orderBy);
    }

    @PutMapping("/update")
    public ResponseResult updateConfig(@RequestBody Config config) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(config) || Objects.isNull(config.getID())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return configService.updateConfig(config);
    }

    @DeleteMapping("/delete")
    public ResponseResult deleteConfig(Integer ID, String userID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0 || !Strings.hasText(userID)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return configService.deleteConfig(ID, userID);
    }
}
