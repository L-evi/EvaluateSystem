package com.project.evaluate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.mapper.CourseDocTaskMapper;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/1/2 22:11
 */
@Service
public class CourseDocTaskServiceImpl implements CourseDocTaskService {
    @Resource
    private CourseDocTaskMapper courseDocTaskMapper;

    @Override
    public ResponseResult searchTeachingDocuments(Map<String, Object> map) {
//        获取map中属于CourseDocTask的数据，就不用一个一个测试是否为空
        CourseDocTask courseDocTask = JSON.parseObject(JSONObject.toJSONString(map), CourseDocTask.class);
//        将实体类转化为map
        Map<String, Object> objectMap = JSON.parseObject(JSON.toJSONString(courseDocTask), Map.class);
//        获取页面
        int pageSize = 10;
        if (map.containsKey("pageSize")) {
            pageSize = Integer.valueOf((String) map.get("pageSize"));
        }
//        获取每一页需要多少数据
        int startPage = 1;
        if (map.containsKey("page")) {
            startPage = Integer.valueOf((String) map.get("page"));
        }
//        开始的序号
        objectMap.put("index", (startPage - 1) * pageSize);
//        读取几个
        objectMap.put("pageSize", pageSize);

//        获取数据
        List<CourseDocTask> courseDocTasks = this.courseDocTaskMapper.screenTeacherCourseDocTask(objectMap);
        if (Objects.isNull(courseDocTasks) || courseDocTasks.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        }
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(courseDocTasks));
        return new ResponseResult(ResultCode.SUCCESS, jsonArray);
    }
}
