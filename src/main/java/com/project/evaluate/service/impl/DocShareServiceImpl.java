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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
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

    /**
     * 临时文件前缀
     */
    @Value("${file.temp-pre-path}")
    private String tempPrePath;

    /**
     * 文件前缀
     */
    @Value("${file.share-pre-path}")
    private String sharePrePath;

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

    @Override
    public ResponseResult submitDocument(DocShare docShare) {
        JSONObject jsonObject = new JSONObject();
//        根据文件路径去找临时文件是否存在
        File tempShareFile = new File(this.tempPrePath + File.separator, docShare.getDocPath());
        if (!tempShareFile.exists()) {
            jsonObject.put("msg", "文件不存在");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        }
//        把文件放入share文件夹中
        File file = new File(this.sharePrePath + File.separator, docShare.getDocPath());
        try (InputStream inputStream = new FileInputStream(tempShareFile);
             OutputStream outputStream = new FileOutputStream(file)) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = inputStream.read(bytes)) > 0) {
                outputStream.write(bytes, 0, length);
            }
        } catch (FileNotFoundException e) {
            jsonObject.put("msg", "文件无法找到");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        } catch (IOException e) {
            jsonObject.put("msg", "IO操作错误");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        }
        jsonObject.put("DocPath", this.sharePrePath + File.separator + docShare.getDocPath());
        jsonObject.put("ID", docShare.getID());
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }
}
