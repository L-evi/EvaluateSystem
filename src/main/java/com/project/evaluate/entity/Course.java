package com.project.evaluate.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import converter.CourseCourseTypeConverter;
import converter.CourseEducationTypeConverter;
import converter.CourseMajorConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    Integer ID;

    /**
     * 课程代码：主键或索引
     */
    @ExcelProperty(value = "课程编码", index = 0)
    String courseID;

    /**
     * 课程名称
     */
    @ExcelProperty(value = "课程名称", index = 1)
    String courseName;
    /**
     * 课程性质：选修课，必修课
     */
    @ExcelProperty(value = "课程性质", index = 5)
    String courseProperty;

    /**
     * 考核类型：考试|考查
     */
    @ExcelProperty(value = "考核类型", index = 6)
    String testType;

    /**
     * 大纲课程类型：外键，关联EducationType的ID
     */
    @ExcelProperty(value = "大纲课程类型", index = 8,converter = CourseEducationTypeConverter.class)
    Integer educationType;

    /**
     * 课程类型：外键，关联CourseType的ID
     */
    @ExcelProperty(value = "课程类型", index = 7,converter = CourseCourseTypeConverter.class)
    Integer courseType;

    /**
     * 学分：精确到一位小数
     */
    @ExcelProperty(value = "学分", index = 4)
    Float credit;

    /**
     * 课程负责人：外键，关联Faculty的userID
     */
    @ExcelProperty(value = "负责教师", index = 2)
    String teamLeader;

    /**
     * 适用专业：外键，关联Major的ID
     */
    @ExcelProperty(value = "适用专业", index = 3,converter = CourseMajorConverter.class)
    Integer major;

    /**
     * 学期：有效值：1~8
     */
    @ExcelIgnore
    Integer courseTerm;

}
