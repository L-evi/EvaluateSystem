package com.project.evaluate.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 16:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDocDetail {
    /*
     * 说明:
     * (1)每提交一个文件，则保存为一个文件提交记录。如果所提交的文件数超过该类型 文档的最大文件数，则不能再提交该类型的文档。
     * (2) 上传文件在服务器的保存路径：
     *      服务器保存路径
     *      --  学期名字：2021-2022-1
     *          --  课程编码_课程名称：21HA4950_JAVA语言程序设计
     *              --  课程编码_教师卡号/userID_教师姓名：21HA4950_张老师
     *                  -- 21HA4950_2001011_李四
     * 上传的文件，保存在每个教师的课程文档目录下，如:21HA4950_20001010_张三
     * 定位文档路径时，应匹配课程代码，而不匹配课程名称，因课程名称可能会发生改变， 但课程编码不会变。
     * 如:对于“21HA0250_JAVA 语言程序设计”，只匹配编码 21HA0250 来 定位路径。
     */

    /**
     * 文件标识：自增
     */
    private int ID;
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
