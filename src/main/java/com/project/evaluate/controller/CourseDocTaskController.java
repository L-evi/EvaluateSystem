package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.service.CourseDocTaskService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:08
 */
@RestController
@CrossOrigin("*")
@RequestMapping(value = "/api/courseDocTask/")
public class CourseDocTaskController {

    @Resource
    private CourseDocTaskService courseDocTaskService;

    @RequestMapping(value = "/search")
    public ResponseResult searchTeachingDocuments(@RequestBody Map<String, Object> map, HttpServletRequest request) {
        String token = request.getHeader("token");
        if (map.containsKey("teacher")) {
            map.remove("teacher");
        }
        if (Strings.hasText(token)) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
                String roleType = (String) jsonObject.get("roleType");
                if (roleType.equals("0")) {
                    map.put("teacher", (String) jsonObject.get("userID"));
                }
            } catch (Exception e) {
                throw new RuntimeException("Parse token  错误");
            }
        }
        return this.courseDocTaskService.searchTeachingDocuments(map);
    }

    //    只有文档管理员才能删除
    @RequiresRoles("2")
    @RequestMapping(value = "/delete")
    public ResponseResult deleteTeachingDocuments(@RequestBody JSONObject jsonObject) {
        String ID = (String) jsonObject.get("ID");
        if (!Strings.hasText(ID)) {
            jsonObject.clear();
            jsonObject.put("msg", "缺少必要参数ID");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.courseDocTaskService.deleteTeachingDocuments(Integer.parseInt(ID));
    }
}
