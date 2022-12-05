package com.project.evaluate.filter;

import com.project.evaluate.util.JwtUtil;
import io.jsonwebtoken.lang.Strings;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description 用于过滤Jwt的过滤器
 * @since 2022/12/5 17:28
 */
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        从头部中获取token
        String token = request.getHeader("token");
        if (!Strings.hasText(token)) {
//            缺少认证信息
            System.out.println("token 缺失");
            return false;
        }
        return JwtUtil.isTimeout(token);
    }
}
