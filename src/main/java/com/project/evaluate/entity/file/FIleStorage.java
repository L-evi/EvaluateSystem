package com.project.evaluate.entity.file;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description 文件存储数据库实体
 * @since 2022/12/6 17:37
 */
@Data
public class FIleStorage implements Serializable {
    /**
     * 主键
     **/
    private Long id;
    /**
     * 文件真实姓名
     **/
    private String realName;
    /**
     * 文件名
     **/
    private String fileName;
    /**
     * 文件后缀
     **/
    private String suffix;
    /**
     * 文件路径
     **/
    private String filePath;
    /**
     * 文件类型
     **/
    private String fileType;
    /**
     * 文件大小
     **/
    private Long size;
    /**
     * 检验码 md5
     **/
    private String identifier;
    /**
     * 创建者
     **/
    private String createBy;
    /**
     * 创建时间
     **/
    private LocalDateTime createTime;
    /**
     * 更新人
     **/
    private String updateBy;
    /**
     * 更新时间
     **/
    private LocalDateTime updateTime;
}
