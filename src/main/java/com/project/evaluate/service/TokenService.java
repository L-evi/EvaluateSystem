package com.project.evaluate.service;

import com.project.evaluate.util.response.ResponseResult;

public interface TokenService {
    ResponseResult getTokenMessage(String token);
}
