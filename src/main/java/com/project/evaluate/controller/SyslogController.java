package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Syslog;
import com.project.evaluate.service.SyslogService;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/25 22:59
 */
@RestController
@RequestMapping(value = "/api/syslog")
public class SyslogController {

    @Resource
    private SyslogService syslogService;

    @GetMapping("/get/page")
    ResponseResult selectPageSyslog(Syslog syslog, Integer page, Integer pageSize, String orderBy, String beforeTime, String afterTime) {
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        if (!Strings.hasText(orderBy)) {
            orderBy = "logTime DESC";
        }
        if (Objects.isNull(syslog)) {
            syslog = new Syslog();
        }
        Date before = null;
        Date after = null;
        if (Strings.hasText(beforeTime)) {
            before = new Date(Long.parseLong(beforeTime));
        }
        if (Strings.hasText(afterTime)) {
            after = new Date(Long.parseLong(afterTime));
        }
        return this.syslogService.selectPageSyslog(syslog, page, pageSize, orderBy, before, after);
    }


    @GetMapping("/get/single")
    ResponseResult selectSyslog(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.syslogService.selectSyslog(ID);
    }

    @DeleteMapping(value = "/delete/single")
    ResponseResult deleteSyslog(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID <= 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.syslogService.deleteSyslog(ID);
    }

    @DeleteMapping(value = "/delete/page")
    ResponseResult deletePageSyslog(String ids) {
        JSONObject jsonObject = new JSONObject();
        if (!Strings.hasText(ids)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        ids = ids.replaceAll(" ", "");
        String[] strings = ids.split(",");
        List<Integer> list = new ArrayList<>();
        for (String string : strings) {
//            判断是不是纯数字
            if (string.chars().allMatch(Character::isDigit)) {
                list.add(Integer.valueOf(string));
            }
        }
        return this.syslogService.deletePageSyslog(list);
    }

}
