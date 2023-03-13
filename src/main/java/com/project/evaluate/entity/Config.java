package com.project.evaluate.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (Config)实体类
 *
 * @author makejava
 * @since 2023-02-03 13:41:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Config implements Serializable {
    private static final long serialVersionUID = 991785607513246898L;

    @JsonProperty("ID")
    private Integer ID;
    /**
     * 教学文档保存路径
     */
    @JsonProperty("teachingDocRoot")
    private String teachingDocRoot;
    /**
     * 共享文档保存路径
     */
    @JsonProperty("shareDocRoot")
    private String shareDocRoot;
    /**
     * 文件存储的最大容量
     */
    @JsonProperty("maxDocStorage")
    private Integer maxDocStorage;
    /**
     * 允许上传文件的最大容量
     */
    @JsonProperty("maxFileSize")
    private Integer maxFileSize;
    /**
     * 允许上传的文件类型/扩展名
     */
    @JsonProperty("allowFileType")
    private String allowFileType;
    /**
     * 忘记密码的提示信息
     */
    @JsonProperty("pwdForgotTips")
    private String pwdForgotTips;
    /**
     * 配置所属的用户ID
     */
    @JsonProperty("userID")
    private String userID;
}

