package com.project.evaluate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.BulletinDao;
import com.project.evaluate.dao.FacultyDao;
import com.project.evaluate.entity.Bulletin;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.service.BulletinService;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/10 14:08
 */
@Service
public class BulletinServiceImpl implements BulletinService {
    @Resource
    private BulletinDao bulletinDao;

    @Resource
    private RedisCache redisCache;

    @Resource
    private FacultyDao facultyDao;

    @Override
    public ResponseResult insertBulletin(Bulletin bulletin) {
        JSONObject jsonObject = new JSONObject();
        Long num = this.bulletinDao.insertBulletin(bulletin);
        if (num < 1) {
            jsonObject.put("msg", "插入错误，数据已存在");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        jsonObject.put("msg", "插入成功");
        jsonObject.put("num", num);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectPageBulletin(Bulletin bulletin, String role, Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        Date date = null;
//        教师和认证专家只能看到未过期的
        if (role == "0" || role == "3") {
            date = new Date();
        }
        PageHelper.startPage(page, pageSize, orderBy);
        List<Bulletin> bulletins = this.bulletinDao.selectByBulletin(bulletin, date);
        if (Objects.isNull(bulletins) || bulletins.isEmpty()) {
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<Bulletin> pageInfo = new PageInfo<Bulletin>(bulletins);
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(pageInfo.getList()));
        jsonObject.put("array", jsonArray);
        jsonObject.put("total", pageInfo.getTotal());
        jsonObject.put("pages", pageInfo.getPages());
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectSingleBulletin(Integer ID) {
        JSONObject jsonObject = new JSONObject();
//        从redis中获取=
        Bulletin bulletin = JSONObject.toJavaObject(this.redisCache.getCacheObject("Bulletin:" + ID), Bulletin.class);
        if (Objects.isNull(bulletin)) {
            bulletin = this.bulletinDao.selectByID(ID);
            if (Objects.isNull(bulletin)) {
                jsonObject.put("msg", "查询结果为空");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
//            存入redis中
            this.redisCache.setCacheObject("Bulletin:" + ID, bulletin, 1, TimeUnit.DAYS);
        }
//        查询发布人的username
        Faculty faculty = JSONObject.toJavaObject(this.redisCache.getCacheObject("Faculty:" + bulletin.getOperator()), Faculty.class);
        if (Objects.isNull(faculty)) {
            faculty = this.facultyDao.selectByUserID(bulletin.getOperator());
            if (Objects.isNull(faculty)) {
                jsonObject.put("msg", "查询结果为空");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
//            存入redis中
            this.redisCache.setCacheObject("Faculty:" + faculty.getUserID(), faculty, 3, TimeUnit.HOURS);
        }
        jsonObject = JSONObject.parseObject(JSON.toJSONString(bulletin));
//        放入操作员名称
        jsonObject.put("username", faculty.getUserName());
        jsonObject.remove("operator");
        jsonObject.put("msg", "查询成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult updateBulletin(Bulletin bulletin) {
        JSONObject jsonObject = new JSONObject();
//        更新数据库
        Long num = this.bulletinDao.updateBulletin(bulletin);
        if (num < 0) {
            jsonObject.put("msg", "更新数据失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        更新redis
        this.redisCache.setCacheObject("Bulletin:" + bulletin.getID(), bulletin, 1, TimeUnit.DAYS);
        jsonObject.put("msg", "更新数据成功");
        jsonObject.put("num", num);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult deleteBulletin(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        Boolean isDelete = this.bulletinDao.deleteByID(ID);
        if (!isDelete) {
            jsonObject.put("msg", "删除失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        redis中删除
        this.redisCache.deleteObject("Bulletin:" + ID);
        jsonObject.put("msg", "删除成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }
}
