package com.project.evaluate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.BulletinDao;
import com.project.evaluate.dao.FacultyDao;
import com.project.evaluate.entity.DO.BulletinDO;
import com.project.evaluate.entity.DO.FacultyDO;
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
    public ResponseResult insertBulletin(BulletinDO bulletinDO) {
        JSONObject jsonObject = new JSONObject();
        Long num = this.bulletinDao.insertBulletin(bulletinDO);
        if (num < 1) {
            jsonObject.put("msg", "插入错误，数据已存在");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        jsonObject.put("msg", "插入成功");
        jsonObject.put("num", num);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectPageBulletin(BulletinDO bulletinDO, Integer role, Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        Date date = null;
//        教师和认证专家只能看到未过期的
        if (role == 0 || role == 3) {
            date = new Date();
        }
        PageHelper.startPage(page, pageSize, orderBy);
        List<BulletinDO> bulletinDOS = this.bulletinDao.selectByBulletin(bulletinDO, date);
        if (Objects.isNull(bulletinDOS) || bulletinDOS.isEmpty()) {
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<BulletinDO> pageInfo = new PageInfo<BulletinDO>(bulletinDOS);
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
        BulletinDO bulletinDO = JSONObject.toJavaObject(this.redisCache.getCacheObject("Bulletin:" + ID), BulletinDO.class);
        if (Objects.isNull(bulletinDO)) {
            bulletinDO = this.bulletinDao.selectByID(ID);
            if (Objects.isNull(bulletinDO)) {
                jsonObject.put("msg", "查询结果为空");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
//            存入redis中
            this.redisCache.setCacheObject("Bulletin:" + ID, bulletinDO, 1, TimeUnit.DAYS);
        }
//        查询发布人的username
        FacultyDO facultyDO = JSONObject.toJavaObject(this.redisCache.getCacheObject("FacultyDO:" + bulletinDO.getOperator()), FacultyDO.class);
        if (Objects.isNull(facultyDO)) {
            facultyDO = this.facultyDao.selectByUserID(bulletinDO.getOperator());
            if (Objects.isNull(facultyDO)) {
                jsonObject.put("msg", "查询结果为空");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
//            存入redis中
            this.redisCache.setCacheObject("FacultyDO:" + facultyDO.getUserID(), facultyDO, 3, TimeUnit.HOURS);
        }
        jsonObject = JSONObject.parseObject(JSON.toJSONString(bulletinDO));
//        放入操作员名称
        jsonObject.put("username", facultyDO.getUserName());
        jsonObject.remove("operator");
        jsonObject.put("msg", "查询成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult updateBulletin(BulletinDO bulletinDO) {
        JSONObject jsonObject = new JSONObject();
//        更新数据库
        Long num = this.bulletinDao.updateBulletin(bulletinDO);
        if (num < 1) {
            jsonObject.put("msg", "更新数据失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        更新redis
        bulletinDO = this.bulletinDao.selectByID(bulletinDO.getID());
        this.redisCache.setCacheObject("Bulletin:" + bulletinDO.getID(), bulletinDO, 1, TimeUnit.DAYS);
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
