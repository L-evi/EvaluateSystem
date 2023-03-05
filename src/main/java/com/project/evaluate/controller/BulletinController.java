package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.Bulletin;
import com.project.evaluate.service.BulletinService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/10 14:09
 */
@RestController
@CrossOrigin("*")
@RequestMapping("/api/bulletin")
public class BulletinController {

    @Resource
    private BulletinService bulletinService;

    @PostMapping(value = "/add")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    public ResponseResult insertBulletin(@RequestBody Bulletin bulletin, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(bulletin)) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
//        如果没有获取operator时候才使用token中的userID
        if (!Strings.hasText(bulletin.getOperator())) {
            String token = request.getHeader("token");
            try {
                jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
                String userID = (String) jsonObject.get("userID");
                bulletin.setOperator(userID);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        bulletin.setCreateTime(new Date());
        return this.bulletinService.insertBulletin(bulletin);
    }

    @GetMapping(value = "/get/single")
    public ResponseResult selectSingleBulletin(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.bulletinService.selectSingleBulletin(ID);
    }

    @GetMapping(value = "/get/page")
    public ResponseResult selectPageBulletin(HttpServletRequest request, Integer page, Integer pageSize, @DefaultValue("issueTime DESC") String orderBy, Bulletin bulletin) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        if (Objects.isNull(bulletin)) {
            bulletin = new Bulletin();
        }
        String token = request.getHeader("token");
        Integer role = null;
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            role = (Integer) jsonObject.get("roleType");
            if (Objects.isNull(role)) {
                jsonObject.clear();
                jsonObject.put("msg", "参数缺失");
                return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.bulletinService.selectPageBulletin(bulletin, role, page, pageSize, orderBy);
    }

    @PutMapping(value = "/update")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    public ResponseResult updateBulletin(@RequestBody Bulletin bulletin, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(bulletin) || Objects.isNull(bulletin.getID()) || bulletin.getID() == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String token = request.getHeader("token");
        try {
            jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
            String userID = (String) jsonObject.get("userID");
            bulletin.setOperator(userID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.bulletinService.updateBulletin(bulletin);
    }

    @DeleteMapping(value = "/delete")
    @RequiresRoles(value = {"1", "2"}, logical = Logical.OR)
    public ResponseResult deleteBulletin(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.bulletinService.deleteBulletin(ID);
    }
}
