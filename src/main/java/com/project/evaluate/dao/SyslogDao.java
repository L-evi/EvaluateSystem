package com.project.evaluate.dao;

import com.project.evaluate.entity.Syslog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface SyslogDao {
    Integer insertSyslog(Syslog syslog);

    Syslog selectByID(Integer ID);

    List<Syslog> selectPageSysLog(@Param("syslog") Syslog syslog, Date beforeTime, Date afterTime);

    Integer deletePageSyslog(@Param("ids") List<Integer> ids);

    Boolean deleteSyslogByID(Integer ID);
}
