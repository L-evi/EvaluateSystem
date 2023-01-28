package com.project.evaluate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (Syslog)实体类
 *
 * @author makejava
 * @since 2023-01-23 14:28:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Syslog implements Serializable {
    private static final long serialVersionUID = -21139683385465480L;
    /**
     * ID
     */
    private Integer id;
    /**
     * 模块名
     */
    private String module;
    /**
     * 操作类型
     */
    private String action;
    /**
     * 操作时间
     */
    private Date logTime;
    /**
     * 操作条件
     */
    private String conditions;
    /**
     * 操作结果
     */
    private String result;
    /**
     * 操作成功失败
     */
    private Integer status;
    /**
     * 操作者
     */
    private String operator;
    /**
     * IP地址
     */
    private String IP;
}

