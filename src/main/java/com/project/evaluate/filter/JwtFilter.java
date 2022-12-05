package com.project.evaluate.filter;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description 用于过滤Jwt的过滤器
 * @since 2022/12/5 17:28
 */
public class JwtFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
