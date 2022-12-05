package com.project.evaluate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description : 对应于数据库User的实体类
 * @since 2022/12/5 1:23
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    /**
     * 登录名
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 登录角色
     */
    private String role;
    /**
     * 用户名
     */
    private String name;
}
