package com.project.evaluate.entity;

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
    
    private Integer ID;
    /**
     * 教学文档保存路径

     */
    private String teachingDocRoot;
    /**
     * 共享文档保存路径

     */
    private String shareDocRoot;
    /**
     * 文件存储的最大容量

     */
    private Integer maxDocStorage;
    /**
     * 允许上传文件的最大容量
     */
    private Integer maxFileSize;
    /**
     * 允许上传的文件类型/扩展名
     */
    private String allowFileType;
    /**
     * 忘记密码的提示信息
     */
    private String pwdForgotTips;
    /**
     * 配置所属的用户ID
     */
    private String userID;
}

