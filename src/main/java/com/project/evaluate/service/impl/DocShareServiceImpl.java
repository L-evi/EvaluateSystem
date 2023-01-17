package com.project.evaluate.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.DocShareDao;
import com.project.evaluate.entity.DocShare;
import com.project.evaluate.service.DocShareService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/14 10:05
 */
@Service
public class DocShareServiceImpl implements DocShareService {

    @Resource
    private RedisCache redisCache;

    @Resource
    private DocShareDao docShareDao;

    @Override
    public ResponseResult addDocShare(DocShare docShare) {
        Long num = this.docShareDao.insertDocShare(docShare);
        JSONObject jsonObject = new JSONObject();
        if (num < 1) {
            jsonObject.put("msg", "插入数据失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        jsonObject.put("msg", "插入成功");
        jsonObject.put("num", num);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectDocShareByID(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        DocShare docShare = JSONObject.toJavaObject(this.redisCache.getCacheObject("DocShare:" + ID), DocShare.class);
        if (Objects.isNull(docShare)) {
            docShare = this.docShareDao.selectDocShare(ID);
            if (Objects.isNull(docShare)) {
                jsonObject.put("msg", "查询失败");
                return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
            }
            this.redisCache.setCacheObject("DocShare:" + ID, docShare, 1, TimeUnit.DAYS);
        }
        jsonObject = JSONObject.parseObject(JSON.toJSONString(docShare));
        jsonObject.put("msg", "查询成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult selectPageDocShare(DocShare docShare, Integer page, Integer pageSize, String orderBy) {
        JSONObject jsonObject = new JSONObject();
        PageHelper.startPage(page, pageSize, orderBy);
        List<DocShare> docShares = this.docShareDao.selectPageDocShare(docShare);
        if (Objects.isNull(docShares) || docShares.isEmpty()) {
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        PageInfo<DocShare> pageInfo = new PageInfo<>(docShares);
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString((pageInfo.getList())));
        jsonObject.put("total", pageInfo.getTotal());
        jsonObject.put("pages", pageInfo.getPages());
        jsonObject.put("array", jsonArray);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult updateDocShare(DocShare docShare, String token) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            String roleType = (String) jsonObject.get("roleType");
            String userID = (String) jsonObject.get("userID");
            if ("2".equals(roleType)) {
//                文档管理员：只能修改自己发布的分享文档
                docShare.setSubmitter(userID);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Long num = this.docShareDao.updateDocShare(docShare);
        jsonObject.clear();
        if (num < 1) {
            jsonObject.put("msg", "修改失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        存入redis中
        docShare = this.docShareDao.selectDocShare(docShare.getID());
        this.redisCache.setCacheObject("DocShare:" + docShare.getID(), docShare, 1, TimeUnit.DAYS);
        jsonObject.put("msg", "修改成功");
        jsonObject.put("num", num);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    @Override
    public ResponseResult deleteDocShare(Integer ID, String token) {
        JSONObject jsonObject = new JSONObject();
        String userID = null;
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            String roleType = (String) jsonObject.get("roleType");
            if ("2".equals(roleType)) {
//                文档管理员：只能修改自己发布的分享文档
                userID = (String) jsonObject.get("userID");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        jsonObject.clear();
        Long num = this.docShareDao.deleteDocShare(ID, userID);
        if (num < 1) {
            jsonObject.put("msg", "删除失败");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        从redis中删除
        this.redisCache.deleteObject("DocShare:" + ID);
        jsonObject.put("msg", "删除成功");
        jsonObject.put("num", num);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }
}
