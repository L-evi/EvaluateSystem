package com.project.evaluate.entity;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 16:24
 */
public class CourseDocDetail {
    /**
     * 文件标识：自增
     */
    private int Id;
    /**
     * 文档所属的提交任务
     */
    private int taskID;
    /**
     * 文件类型ID
     */
    private int docTypeID;
    /**
     * 文件存储位置
     */
    private String docPath;
    /**
     * 文件提交时间
     */
    private String uploadTime;
    /**
     * 文档提交人
     */
    private String submitter;


}
