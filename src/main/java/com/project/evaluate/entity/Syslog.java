package com.project.evaluate.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import converter.SyslogStatusConverter;
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
    @ExcelProperty("编号")
    private Integer id;
    /**
     * 模块名
     */
    @ExcelProperty("模块名")
    private String module;
    /**
     * 操作类型
     */
    @ExcelProperty("操作类型")
    private String action;
    /**
     * 操作时间
     */
    @ExcelProperty("操作时间")
    private Date logTime;
    /**
     * 操作条件
     */
    @ExcelProperty("请求条件")
    private String conditions;
    /**
     * 操作结果
     */
    @ExcelProperty("响应结果")
    private String result;
    /**
     * 操作成功失败
     */
    @ExcelProperty(value = "操作状态", converter = SyslogStatusConverter.class)
    private Integer status;
    /**
     * 操作者
     */
    @ExcelProperty("操作者")
    private String operator;
    /**
     * IP地址
     */
    @ExcelProperty("IP地址")
    private String IP;
}

