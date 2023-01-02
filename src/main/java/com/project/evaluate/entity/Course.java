package com.project.evaluate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description Course表的实体类
 * @since 2023/1/2 20:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Course implements Serializable {

    /**
     * ID：自增
     */
    int id;

    /**
     * 课程代码：主键或索引
     */
    String courseId;

    /**
     * 考核类型：考试|考查
     */
    String testType;

    /**
     * 大纲课程类型：外键，关联EducationType的ID
     */
    int educationType;

    /**
     * 课程类型：外键，关联CourseType的ID
     */
    int courseType;

    /**
     * 学分：精确到一位小数
     */
    Float credit;

    /**
     * 课程负责人：外键，关联Faculty的userID
     */
    String teamLeader;

    /**
     * 适用专业：外键，关联Major的ID
     */
    int major;

    /**
     * 学期：有效值：1~8
     */
    int courseTerm;

}
