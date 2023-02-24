package com.project.evaluate.dao;

import com.project.evaluate.entity.DO.ConfigDO;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/2/3 13:53
 */
@Repository
@Mapper
public interface ConfigDao {
    ConfigDO selectByID(Integer ID);

    ConfigDO selectByUserID(String userID);

    ConfigDO selectDefault();

    Long insertConfig(ConfigDO configDO);

    List<ConfigDO> selectPageConfig();

    Boolean deleteConfig(Integer ID,String userID);

    Boolean updateConfig(ConfigDO configDO);
}
