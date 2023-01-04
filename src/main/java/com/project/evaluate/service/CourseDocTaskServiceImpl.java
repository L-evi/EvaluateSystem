package com.project.evaluate.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Course;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.mapper.CourseDocDetailMapper;
import com.project.evaluate.mapper.CourseDocTaskMapper;
import com.project.evaluate.mapper.CourseMapper;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private CourseMapper courseMapper;


    @Resource
    private CourseDocDetailMapper courseDocDetailMapper;

    @Resource
    private RedisCache redisCache;

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
        List<Map<String, Object>> taskMaps = new ArrayList<>();
//         每一个都获取其中的课程名称
        courseDocTasks.forEach(task -> {
            Map<String, Object> taskMap = JSON.parseObject(JSON.toJSONString(task), Map.class);
            Course course = null;
//            从redis中获取数据
            course = JSONObject.toJavaObject(this.redisCache.getCacheObject("Course:" + task.getCourseId()), Course.class);
//            如果没有获取到则从数据库中获取
            if (Objects.isNull(course)) {
                course = this.courseMapper.selectByCourseID(task.getCourseId());
                this.redisCache.setCacheObject("Course:" + course.getCourseId(), course);
            }
            taskMap.put("courseName", course.getCourseName());
            taskMaps.add(taskMap);
        });

        if (Objects.isNull(courseDocTasks) || courseDocTasks.isEmpty()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "查询结果为空");
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        }

        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(taskMaps));

        return new ResponseResult(ResultCode.SUCCESS, jsonArray);
    }

    @Override
    public ResponseResult deleteTeachingDocuments(int ID) {
        JSONObject jsonObject = new JSONObject();
        Map<String, Object> map = new HashMap<>();
        map.put("taskID", ID);
        map.put("page", 1);
        map.put("pageSize", 10);
        List<CourseDocDetail> courseDocDetails = this.courseDocDetailMapper.selectByTaskID(map);
//        教学文档任务已经上传文件了
        if (!courseDocDetails.isEmpty()) {
            jsonObject.put("msg", "教学文档文件已上传文件，无法删除");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
        CourseDocTask courseDocTask = this.courseDocTaskMapper.selectByID(ID);
        Date now = new Date();
//        如果任务超时 或者 任务已经关闭了
        if (now.after(courseDocTask.getDeadline()) || courseDocTask.getCloseTask() == 1) {
            jsonObject.put("msg", "任务已经过期或已经关闭，无法删除");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
//        没有问题了才删除
        Long num = this.courseDocTaskMapper.deleteTaskByID(ID);
        if (num > 0) {
            jsonObject.put("msg", "删除成功");
            jsonObject.put("count", num);
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        }
        return new ResponseResult(ResultCode.DATABASE_ERROR);
    }
}
