package com.project.evaluate.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.evaluate.converter.CourseCourseTypeConverter;
import com.project.evaluate.converter.CourseEducationTypeConverter;
import com.project.evaluate.converter.CourseMajorConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode
public class Course implements Serializable {

    /**
     * id：主键或者索引
     */
    @ExcelIgnore
    @JsonProperty("ID")
    Integer ID;

    /**
     * 课程代码：主键或索引
     */
    @ExcelProperty(value = "课程编码", index = 0)
    @JsonProperty("courseID")
    String courseID;

    /**
     * 课程名称
     */
    @ExcelProperty(value = "课程名称", index = 1)
    @JsonProperty("courseName")
    String courseName;
    /**
     * 课程性质：选修课，必修课
     */
    @ExcelProperty(value = "课程性质", index = 5)
    @JsonProperty("courseProperty")
    String courseProperty;

    /**
     * 考核类型：考试|考查
     */
    @ExcelProperty(value = "考核类型", index = 6)
    @JsonProperty("testType")
    String testType;

    /**
     * 大纲课程类型：外键，关联EducationType的ID
     */
    @ExcelProperty(value = "大纲课程类型", index = 8, converter = CourseEducationTypeConverter.class)
    @JsonProperty("educationType")
    Integer educationType;

    /**
     * 课程类型：外键，关联CourseType的ID
     */
    @ExcelProperty(value = "课程类型", index = 7, converter = CourseCourseTypeConverter.class)
    @JsonProperty("courseType")
    Integer courseType;

    /**
     * 学分：精确到一位小数
     */
    @ExcelProperty(value = "学分", index = 4)
    @JsonProperty("credit")
    Float credit;

    /**
     * 课程负责人：外键，关联Faculty的userID
     */
    @ExcelProperty(value = "负责教师", index = 2)
    @JsonProperty("teamLeader")
    String teamLeader;

    /**
     * 适用专业：外键，关联Major的ID
     */
    @ExcelProperty(value = "适用专业", index = 3, converter = CourseMajorConverter.class)
    @JsonProperty("major")
    Integer major;

    /**
     * 学期：有效值：1~8
     */
    @ExcelIgnore
    @JsonProperty("courseTerm")
    Integer courseTerm;

}
