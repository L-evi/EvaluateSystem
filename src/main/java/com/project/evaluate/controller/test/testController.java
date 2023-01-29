package com.project.evaluate.controller.test;

import com.project.evaluate.dao.FacultyDao;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.util.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(value = "*")
@RestController
@RequestMapping(value = "/test")
public class testController {
    @Resource
    private FacultyDao facultyDao;

    //    @RateLimiter(value = 1.0, timeout = 100)
    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public Map<String, Object> testHelloWorld(@RequestBody Map<String, Object> getMessage) {
        Map<String, Object> res = new HashMap<>();
//        将getMessage中的所有内容加入到res中
        getMessage.forEach((key, value) -> res.putIfAbsent(key, value));
        res.put("mag", "Hello World!");
        return res;
    }

    @RequestMapping(value = "/update")
    public ResponseResult testUpdate() {
        Faculty faculty = this.facultyDao.selectByUserID("user");
        faculty.setLastLoginIP("127.0.0.1");
        int i = this.facultyDao.updateFaculty(faculty);
        System.out.println(i);
        return ResponseResult.success();
    }


    @RequestMapping(value = "/helloget", method = RequestMethod.GET)
    public ResponseResult testGet() {
        return ResponseResult.success();
    }
}
