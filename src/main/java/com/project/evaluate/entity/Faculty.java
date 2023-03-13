package com.project.evaluate.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.evaluate.converter.FacultyIsInitPwdConverter;
import com.project.evaluate.converter.FacultyRoleTypeConverter;
import com.project.evaluate.converter.FacultyStatusConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class Faculty implements Serializable {
    /**
     * 登录名
     */
    @ExcelProperty(value = "账户ID", index = 0)
    @JsonProperty("userID")
    private String userID;
    /**
     * 登录密码
     */
    @ExcelIgnore
    @JsonProperty("password")
    private String password;
    /**
     * 登录角色
     */
    @ExcelProperty(value = "用户角色", index = 1, converter = FacultyRoleTypeConverter.class)
    @JsonProperty("roleType")
    private Integer roleType;
    /**
     * 用户名
     */
    @ExcelProperty(value = "用户名", index = 2)
    @JsonProperty("username")
    private String username;
    /**
     * 手机号码
     */
    @ExcelProperty(value = "手机号码", index = 3)
    @JsonProperty("mobile")
    private String mobile;
    /**
     * 电子邮箱
     */
    @ExcelProperty(value = "电子邮箱", index = 4)
    @JsonProperty("email")
    private String email;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注", index = 5)
    @JsonProperty("memo")
    private String memo;
    /**
     * 账户状态：0-禁用，1-正常
     */
    @ExcelProperty(value = "账户状态", index = 6, converter = FacultyStatusConverter.class)
    @JsonProperty("status")
    private Integer status;
    /**
     * 是否为初始密码：0-不是，1-是
     */
    @ExcelProperty(value = "初始密码状态", index = 7, converter = FacultyIsInitPwdConverter.class)
    @JsonProperty("isInitPwd")
    private Integer isInitPwd;
    /**
     * 上一次登录的IP地址
     */
    @ExcelProperty(value = "上次登录IP", index = 8)
    @JsonProperty("lastLoginIP")
    private String lastLoginIP;
    /**
     * 上一次登录时间
     */
    @ExcelProperty(value = "上一次登录时间", index = 9)
    @JsonProperty("lastLoginTime")
    private Date lastLoginTime;
    /**
     * 本次登录IP
     */
    @ExcelProperty(value = "本次登录IP", index = 10)
    @JsonProperty("loginIP")
    private String loginIP;
    /**
     * 本次登录时间
     */
    @ExcelProperty(value = "本次登录时间", index = 11)
    @JsonProperty("loginTime")
    private Date loginTime;

}
