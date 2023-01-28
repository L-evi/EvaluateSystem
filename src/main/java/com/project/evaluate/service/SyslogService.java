package com.project.evaluate.service;

import com.project.evaluate.entity.Syslog;
import com.project.evaluate.util.response.ResponseResult;

import java.util.Date;
import java.util.List;

public interface SyslogService {
    ResponseResult selectPageSyslog(Syslog syslog, Integer page, Integer pageSize, String orderBy, Date beforeTime, Date afterTime);

    ResponseResult selectSyslog(Integer ID);

    ResponseResult deleteSyslog(Integer ID);

    ResponseResult deletePageSyslog(List<Integer> list);
}