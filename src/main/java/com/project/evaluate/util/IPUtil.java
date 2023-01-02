package com.project.evaluate.util;

import javax.servlet.http.HttpServletRequest;

public class IPUtil {
    /**
     * @param : request: HttpServletRequest请求
     * @return : 返回一个String：访问后端的IP地址
     * @description : 通过解析请求，获取请求来源的IP地址
     * @author : Levi
     * @since : 2022/7/14 15:57
     */
    public static String getIPAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-real-ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}