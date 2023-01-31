package com.project.evaluate.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.SyslogDao;
import com.project.evaluate.entity.Syslog;
import com.project.evaluate.service.SyslogService;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/25 23:00
 */
@Service
public class SyslogServiceImpl implements SyslogService {
    @Resource
    private RedisCache redisCache;

    @Resource
    private SyslogDao syslogDao;

    @Value("${file.temp-pre-path}")
    private String tempPrePath;

    @Override
    public ResponseResult selectPageSyslog(Syslog syslog, Integer page, Integer pageSize, String orderBy, Date beforeTime, Date afterTime) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(page, pageSize, orderBy);
        List<Syslog> syslogs = this.syslogDao.selectPageSysLog(syslog, beforeTime, afterTime);
        if (Objects.isNull(syslogs) || syslogs.isEmpty()) {
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<Syslog> pageInfo = new PageInfo<>(syslogs);
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(pageInfo.getList()));
        jsonObject.put("array", jsonArray);
        jsonObject.put("total", pageInfo.getTotal());
        jsonObject.put("pages", pageInfo.getPages());
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectSyslog(Integer ID) {
        JSONObject jsonObject = new JSONObject();
//        从redis中获取
        Syslog syslog = JSONObject.toJavaObject(this.redisCache.getCacheObject("Syslog:" + ID), Syslog.class);
        if (Objects.isNull(syslog)) {
            syslog = this.syslogDao.selectByID(ID);
            if (Objects.isNull(syslog)) {
                jsonObject.put("msg", "查询结果为空");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
//           存入redis中
            this.redisCache.setCacheObject("Syslog:" + ID, syslog);
        }
        jsonObject = JSONObject.parseObject(JSON.toJSONString(syslog));
        jsonObject.put("msg", "查询成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult deleteSyslog(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        Boolean isDelete = this.syslogDao.deleteSyslogByID(ID);
        if (!isDelete) {
            jsonObject.put("msg", "删除失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        从redis中删除
        this.redisCache.deleteObject("Syslog:" + ID);
        jsonObject.put("msg", "删除成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult deletePageSyslog(List<Integer> list) {
        JSONObject jsonObject = new JSONObject();
        Integer num = this.syslogDao.deletePageSyslog(list);
        if (num < 1) {
            jsonObject.put("msg", "删除失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        从redis中删除
        for (Integer id : list) {
            this.redisCache.deleteObject("Syslog:" + id);
        }
        jsonObject.put("msg", "删除成功");
        jsonObject.put("num", num);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult exportSyslog(Syslog syslog, Integer page, Integer pageSize, String orderBy, Date beforeTime, Date afterTime) {
        JSONObject jsonObject = new JSONObject();
        if (pageSize != 0) {
            PageHelper.startPage(page, pageSize, orderBy);
        } else {
//            pageSize == 0 时返回所有结果
            PageHelper.startPage(page, pageSize, false, null, true);
        }
        List<Syslog> syslogs = this.syslogDao.selectPageSysLog(syslog, beforeTime, afterTime);
        if (Objects.isNull(syslogs) || syslogs.isEmpty()) {
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<Syslog> pageInfo = new PageInfo<>(syslogs);
        List<Syslog> list = pageInfo.getList();
//        导出为excel
        String filename = this.tempPrePath + File.separator + System.currentTimeMillis() + ".xlsx";
        EasyExcel.write(filename, Syslog.class).sheet("系统日志").doWrite(list);
        File file = new File(filename);
        if (file.exists()) {
            jsonObject.put("msg", "导出成功");
            jsonObject.put("filename", filename);
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        } else {
            jsonObject.put("msg", "导出失败");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        }
    }
}
