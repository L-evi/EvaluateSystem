package com.project.evaluate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description : 对应于数据库User的实体类
 * @since 2022/12/5 1:23
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Faculty implements Serializable {
    /**
     * 登录名
     */
    private String userId;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 登录角色
     */
    private String roleType;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 手机号码
     */
    private String mobile;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 账户状态：0-禁用，1-正常
     */
    private int status;
    /**
     * 是否为初始密码：0-不是，1-是
     */
    private int isInitPwd;
    /**
     * 上一次登录的IP地址
     */
    private String lastLoginIp;
    /**
     * 上一次登录时间
     */
    private Date lastLoginTime;
    /**
     * 本次登录IP
     */
    private String loginIp;
    /**
     * 本次登录时间
     */
    private Date loginTime;
    /**
     * 备注
     */
    private String memo;
}
