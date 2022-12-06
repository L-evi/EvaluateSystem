package com.project.evaluate.controller;

import com.project.evaluate.entity.User;
import com.project.evaluate.service.UserService;
import com.project.evaluate.util.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 02:06
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = "application/json")
    public ResponseResult userLogin(@RequestBody Map<String, Object> dataMap) {
//        获取其中的数据
        User user = new User();
        user.setUsername((String) dataMap.get("username"));
        user.setPassword((String) dataMap.get("password"));
        user.setRole("user");
        return userService.userLogin(user);
    }
}
