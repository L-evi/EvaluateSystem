package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.service.CourseDocTaskService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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

    @GetMapping(value = "/search")
    public ResponseResult selectPageCourseDocTask(CourseDocTask courseDocTask, Integer page, Integer pageSize, String orderBy, HttpServletRequest request) {
        if (Objects.isNull(page)) {
            page = 0;
        }
        if (Objects.isNull(pageSize) || pageSize == 0) {
            pageSize = 10;
        }
        if (!Strings.hasText(orderBy)) {
            orderBy = "ID ASC";
        }
        String token = request.getHeader("token");
        if (Strings.hasText(token)) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(JwtUtil.parseJwt(token).getSubject());
                String roleType = (String) jsonObject.get("roleType");
                if (roleType.equals("0")) {
                    courseDocTask.setTeacher((String) jsonObject.get("userID"));
                }
            } catch (Exception e) {
                throw new RuntimeException("Parse token  错误");
            }
        }
        return this.courseDocTaskService.selectPageCourseDocTask(courseDocTask, page, pageSize, orderBy);
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

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public ResponseResult submit(@RequestBody Map<String, Object> map) throws IOException {
        JSONObject jsonObject = new JSONObject();
        if (!map.containsKey("FileName") || !map.containsKey("taskID") || !map.containsKey("teachingDocRoot")) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseDocTaskService.submitDocument(map);
    }

}
