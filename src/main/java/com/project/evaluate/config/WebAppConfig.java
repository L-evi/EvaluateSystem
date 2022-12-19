package com.project.evaluate.config;

import com.project.evaluate.filter.JwtInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description 添加Jwt过滤器
 * @since 2022/12/6 01:49
 */
@Configuration
public class WebAppConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        注册过滤器
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(new JwtInterceptor());
//        设置拦截路径：所有路径
        interceptorRegistration.addPathPatterns("/**");
//        添加不拦截的路径：测试页面、登录、验证
        interceptorRegistration.excludePathPatterns(
                "/test/**",
                "/user/login",
                "/verify/**",
                "/**"
        );
    }
}
