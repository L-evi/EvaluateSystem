package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.DataLog;
import com.project.evaluate.entity.DO.CourseDocTaskDO;
import com.project.evaluate.service.CourseDocTaskService;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
    public ResponseResult selectPageCourseDocTask(CourseDocTaskDO courseDocTaskDO, Integer page, Integer pageSize, String orderBy, HttpServletRequest request) {
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
                    courseDocTaskDO.setTeacher((String) jsonObject.get("userID"));
                }
            } catch (Exception e) {
                throw new RuntimeException("Parse token  错误");
            }
        }
        return this.courseDocTaskService.selectPageCourseDocTask(courseDocTaskDO, page, pageSize, orderBy);
    }

    //    只有文档管理员才能删除
    @RequiresRoles("2")
    @DeleteMapping(value = "/delete")
    public ResponseResult deleteTeachingDocuments(Integer ID) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return this.courseDocTaskService.deleteTeachingDocuments(ID);
    }


    @PutMapping("/update")
    @RequiresRoles(value = {"2"}, logical = Logical.OR)
    @DataLog(operationType = "update", modelName = "修改文档上传任务")
    public ResponseResult updateCourseDocTask(@RequestBody CourseDocTaskDO courseDocTaskDO) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(courseDocTaskDO) || Objects.isNull(courseDocTaskDO.getID())) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseDocTaskService.updateCourseDocTask(courseDocTaskDO);
    }

    @RequiresRoles(value = "2", logical = Logical.OR)
    @PutMapping("/reset")
    @DataLog(modelName = "重启文档上传任务", operationType = "update")
    public ResponseResult resetCourseDocTask(Integer ID, Integer status) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(ID) || ID == 0) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        return courseDocTaskService.resetCourseDocTask(ID, status);
    }

    @PostMapping("/add")
    public ResponseResult insertCourseDocTask(@RequestBody List<CourseDocTaskDO> courseDocTaskDOS) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(courseDocTaskDOS) || courseDocTaskDOS.isEmpty()) {
            jsonObject.put("msg", "参数缺失");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        courseDocTaskDOS.forEach(System.out::println);
        return ResponseResult.success();
    }
}
