package com.project.evaluate.service;

import com.project.evaluate.entity.DO.ConfigDO;
import com.project.evaluate.util.response.ResponseResult;

public interface ConfigService {
    ResponseResult selectByID(Integer ID);

    ResponseResult selectByUserID(String userID);

    ResponseResult selectPageConfig(Integer page, Integer pageSize, String orderBy);

    ResponseResult insertConfig(ConfigDO configDO);

    ResponseResult deleteConfig(Integer ID, String userID);

    ResponseResult updateConfig(ConfigDO configDO);
}
