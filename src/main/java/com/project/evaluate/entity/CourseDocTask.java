package com.project.evaluate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description 课程达成度计算任务表的实体类
 * @since 2023/1/2 21:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDocTask implements Serializable {

    /**
     * ID：自增
     */
    int id;

    /**
     * 课程代码：外键，关联Course的courseID
     */
    String courseId;

    /**
     * 任课老师：外键，关联Faculty的userID
     */
    String teacher;

    /**
     * 开始学年
     */
    int schoolStartYear;

    /**
     * 结束学年
     */
    int schoolEndYear;

    /**
     * 学期：1--上学期；2--下学期
     */
    int schoolTerm;

    /**
     * 选修班级：如19计算机科学与技术（师范），专业名称由major表提供，其余信息手工输入
     */
    String grades;

    /**
     * 选修人数
     */
    int studentNum;

    /**
     * 发布任务时间：设置任务状态为1时，设置发布任务时间
     */
    Date issueTime;

    /**
     * 截止提交时间
     */

    Date deadline;

    /**
     * 任务状态：0--编辑任务：未正式发布（用户端无法看到该任务）；1--正式发布：等待上传（用户端可以看到上传任务）；2--已上传部分文档；3--已上传全部文档
     */
    int taskStatus;

    /**
     * 关闭任务：对于已完成上传的资料，可设置关闭任务，关闭任务之后，将不可以编辑任务记录、上传和删除文档
     */
    int closeTask;

    /**
     * 操作员：外键，关联Faculty的userID，创建提交任务的操作员，通常是文档的管理员
     */
    String operator;

    /**
     * 备注
     */
    String memo;

}
