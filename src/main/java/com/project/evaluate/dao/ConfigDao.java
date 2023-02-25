package com.project.evaluate.dao;

import com.project.evaluate.entity.Config;


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
    Config selectByID(Integer ID);

    Config selectByUserID(String userID);

    Config selectDefault();

    Long insertConfig(Config config);

    List<Config> selectPageConfig();

    Boolean deleteConfig(Integer ID,String userID);

    Boolean updateConfig(Config config);
}
