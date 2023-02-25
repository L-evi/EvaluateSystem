package com.project.evaluate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (DocShare)实体类
 *
 * @author makejava
 * @since 2023-01-14 09:42:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocShare implements Serializable {
    private static final long serialVersionUID = 791804373195586987L;
    /**
     * ID
     */
    private Integer ID;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档简要说明
     */
    private String desc;
    /**
     * 文件保存路径
     */
    private String docPath;
    /**
     * 文档大小
     */
    private Integer docSize;
    /**
     * 提交时间
     */
    private Date uploadTime;
    /**
     * 提交人
     */
    private String submitter;


}

