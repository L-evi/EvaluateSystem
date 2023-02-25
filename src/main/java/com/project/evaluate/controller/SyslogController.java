package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.Syslog;
import com.project.evaluate.service.SyslogService;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
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
    @RequiresRoles(value = "1", logical = Logical.OR)
    @DataLog(modelName = "分页查询系统日志", operationType = "select")
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
    @RequiresRoles(value = "1", logical = Logical.OR)
    @DataLog(modelName = "查询系统日志详情", operationType = "select")
    ResponseResult selectSyslog(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.syslogService.selectSyslog(ID);
    }

    @DeleteMapping(value = "/delete/single")
    @RequiresRoles(value = "1", logical = Logical.OR)
    @DataLog(modelName = "删除单个系统日志", operationType = "delete")
    ResponseResult deleteSyslog(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID <= 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.syslogService.deleteSyslog(ID);
    }

    @DeleteMapping(value = "/delete/page")
    @RequiresRoles(value = "1", logical = Logical.OR)
    @DataLog(modelName = "批量删除系统日志", operationType = "delete")
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

    //    导出EXCEL
    @GetMapping(value = "/export")
    @RequiresRoles(value = "1", logical = Logical.OR)
    @DataLog(operationType = "select", modelName = "导出日志为EXCEL")
    public ResponseResult exportSyslog(Syslog syslog, Integer page, Integer pageSize, String orderBy, String beforeTime, String afterTime) {
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 0;
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
        return this.syslogService.exportSyslog(syslog, page, pageSize, orderBy, before, after);
    }

}
