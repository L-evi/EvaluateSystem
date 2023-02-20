package com.project.evaluate.entity;

import com.alibaba.excel.annotation.ExcelProperty;
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
     * ID：自增
     */
    Integer ID;
    /**
     * 课程代码：主键或索引
     */
    @ExcelProperty("课程编码")
    String courseID;

    /**
     * 课程名称
     */
    @ExcelProperty("课程名称")
    String courseName;
    /**
     * 课程性质：理论、实验
     */
    @ExcelProperty("课程")
    String courseProperty;

    /**
     * 考核类型：考试|考查
     */
    @ExcelProperty("课程名称")
    String testType;

    /**
     * 大纲课程类型：外键，关联EducationType的ID
     */
    @ExcelProperty("课程名称")
    Integer educationType;

    /**
     * 课程类型：外键，关联CourseType的ID
     */
    @ExcelProperty("课程名称")
    Integer courseType;

    /**
     * 学分：精确到一位小数
     */
    @ExcelProperty("课程名称")
    Float credit;

    /**
     * 课程负责人：外键，关联Faculty的userID
     */
    @ExcelProperty("课程名称")
    String teamLeader;

    /**
     * 适用专业：外键，关联Major的ID
     */
    @ExcelProperty("课程名称")
    Integer major;

    /**
     * 学期：有效值：1~8
     */
    @ExcelProperty("课程名称")
    Integer courseTerm;

}
