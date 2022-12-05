package com.project.evaluate.service;

import com.project.evaluate.entity.User;
import com.project.evaluate.util.response.ResponseResult;

public interface UserService {
    ResponseResult userLogin(User user);
}
