package com.project.evaluate.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class CourseDocTask implements Serializable {

    /**
     * ID：自增
     */
    @ExcelIgnore
    @JsonProperty("ID")
    Integer ID;

    /**
     * 课程代码：外键，关联Course的courseID
     */
    @ExcelProperty("课程代码")
    @JsonProperty("courseID")
    String courseID;

    /**
     * 任课老师：外键，关联Faculty的userID
     */
    @ExcelProperty("任课老师")
    @JsonProperty("teacher")
    String teacher;

    /**
     * 开始学年
     */
    @ExcelProperty("开始学年")
    @JsonProperty("schoolStartYear")
    Integer schoolStartYear;

    /**
     * 结束学年
     */
    @ExcelProperty("结束学年")
    @JsonProperty("schoolEndYear")
    Integer schoolEndYear;

    /**
     * 学期：1--上学期；2--下学期
     */
    @ExcelProperty("学期")
    @JsonProperty("schoolTerm")
    Integer schoolTerm;

    /**
     * 选修班级：如19计算机科学与技术（师范），专业名称由major表提供，其余信息手工输入
     */
    @ExcelProperty("班级组成")
    @JsonProperty("grades")
    String grades;

    /**
     * 选修人数
     */
    @ExcelProperty("已选人数")
    @JsonProperty("studentNum")
    Integer studentNum;

    /**
     * 发布任务时间：设置任务状态为1时，设置发布任务时间
     */
    @ExcelIgnore
    @JsonProperty("issueTime")
    Date issueTime;

    /**
     * 截止提交时间
     */
    @ExcelIgnore
    @JsonProperty("deadLine")
    Date deadline;

    /**
     * 任务状态：0--编辑任务：未正式发布（用户端无法看到该任务）；1--正式发布：等待上传（用户端可以看到上传任务）；2--已上传部分文档；3--已上传全部文档
     */
    @ExcelIgnore
    @JsonProperty("taskStatus")
    Integer taskStatus;

    /**
     * 关闭任务：对于已完成上传的资料，可设置关闭任务，关闭任务之后，将不可以编辑任务记录、上传和删除文档
     */
    @ExcelIgnore
    @JsonProperty("closeTask")
    Integer closeTask;

    /**
     * 操作员：外键，关联Faculty的userID，创建提交任务的操作员，通常是文档的管理员
     */
    @ExcelIgnore
    @JsonProperty("operator")
    String operator;

    /**
     * 备注
     */
    @ExcelIgnore
    @JsonProperty("memo")
    String memo;

}
