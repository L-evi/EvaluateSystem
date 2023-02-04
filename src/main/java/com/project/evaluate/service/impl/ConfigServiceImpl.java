package com.project.evaluate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.ConfigDao;
import com.project.evaluate.entity.Config;
import com.project.evaluate.service.ConfigService;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/2/3 14:05
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ConfigDao configDao;

    @Resource
    private RedisCache redisCache;

    @Override
    public ResponseResult selectByID(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        Config config = JSONObject.toJavaObject(redisCache.getCacheObject("ConfigID:" + ID), Config.class);
//        从redis里面存取
        if (Objects.isNull(config)) {
            config = configDao.selectByID(ID);
            if (Objects.isNull(config)) {
                jsonObject.put("msg", "查询数据失败");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
            redisCache.setCacheObject("ConfigID:" + ID, config, 1, TimeUnit.DAYS);
        }
        jsonObject = JSONObject.parseObject(JSON.toJSONString(config));
        jsonObject.put("msg", "查询数据成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectByUserID(String userID) {
        JSONObject jsonObject = new JSONObject();
        Config config = JSONObject.toJavaObject(redisCache.getCacheObject("ConfigUserID:" + userID), Config.class);
//        从redis里面存储
        if (Objects.isNull(config)) {
            config = configDao.selectByUserID(userID);
            if (Objects.isNull(config)) {
                config = configDao.selectDefault();
                if (Objects.isNull(config)) {
                    jsonObject.put("msg", "参数错误");
                    return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
                }
                redisCache.setCacheObject("ConfigID:" + config.getID(), config, 1, TimeUnit.DAYS);
            } else {
                redisCache.setCacheObject("ConfigUserID:" + userID, config, 1, TimeUnit.DAYS);
            }
        }
        jsonObject = JSONObject.parseObject(JSON.toJSONString(config));
        jsonObject.put("msg", "查询成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectPageConfig(Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(page, pageSize, orderBy);
        List<Config> configs = configDao.selectPageConfig();
        if (Objects.isNull(configs) || configs.isEmpty()) {
            jsonObject.put("msg", "查询失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<Config> configPageInfo = new PageInfo<>(configs);
        jsonObject.put("msg", "查询成功");
        jsonObject.put("pages", configPageInfo.getPages());
        jsonObject.put("total", configPageInfo.getTotal());
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(configPageInfo.getList()));
        jsonObject.put("array", jsonArray);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult insertConfig(Config config) {
        JSONObject jsonObject = new JSONObject();
        Long ID = configDao.insertConfig(config);
        if (ID < 1) {
            jsonObject.put("msg", "插入数据失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        存入redis中
        redisCache.setCacheObject("ConfigID:" + ID, config, 1, TimeUnit.DAYS);
        if (Strings.hasText(config.getUserID())) {
            redisCache.setCacheObject("ConfigUserID:" + config.getUserID(), config, 1, TimeUnit.DAYS);
        }
        jsonObject.put("msg", "插入数据成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult deleteConfig(Integer ID, String userID) {
        JSONObject jsonObject = new JSONObject();
        redisCache.deleteObject("ConfigID:" + ID);
        redisCache.deleteObject("ConfigUserID:" + userID);
        Boolean isDelete = configDao.deleteConfig(ID, userID);
        if (!isDelete) {
            jsonObject.put("msg", "数据删除失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        jsonObject.put("msg", "数据删除成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult updateConfig(Config config) {
        JSONObject jsonObject = new JSONObject();
        Boolean isOk = configDao.updateConfig(config);
        if (!isOk) {
            jsonObject.put("msg", "更新数据失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        config = configDao.selectByID(config.getID());
        redisCache.setCacheObject("ConfigID:" + config.getID(), config, 1, TimeUnit.DAYS);
        if (Strings.hasText(config.getUserID())) {
            redisCache.setCacheObject("ConfigUserID:" + config.getUserID(), config, 1, TimeUnit.DAYS);
        } else {
            redisCache.deleteObject("ConfigUserID:" + config.getUserID());
        }
        jsonObject.put("msg", "更新数据成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }
}
