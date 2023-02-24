package com.project.evaluate.entity.AO;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public
class CourseAO implements Serializable {

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
    @ExcelProperty(value = "大纲课程类型", index = 8)
    String educationType;

    /**
     * 课程类型：外键，关联CourseType的ID
     */
    @ExcelProperty(value = "课程类型", index = 7)
    String courseType;

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
    @ExcelProperty(value = "适用专业", index = 3)
    String major;

    /**
     * 学期：有效值：1~8
     */
    @ExcelIgnore
    Integer courseTerm;

}