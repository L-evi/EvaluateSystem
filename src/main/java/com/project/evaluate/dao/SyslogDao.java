package com.project.evaluate.dao;

import com.project.evaluate.entity.DO.SyslogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface SyslogDao {
    Integer insertSyslog(SyslogDO syslogDO);

    SyslogDO selectByID(Integer ID);

    List<SyslogDO> selectPageSysLog(@Param("syslogDO") SyslogDO syslogDO, Date beforeTime, Date afterTime);

    Integer deletePageSyslog(@Param("ids") List<Integer> ids);

    Boolean deleteSyslogByID(Integer ID);
}
