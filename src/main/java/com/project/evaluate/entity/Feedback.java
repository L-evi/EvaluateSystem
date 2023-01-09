package com.project.evaluate.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (Feedback)实体类
 *
 * @author makejava
 * @since 2023-01-09 17:16:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback implements Serializable {
    private static final long serialVersionUID = 948164130695027273L;
    /**
     * ID
     */
    private Integer id;
    /**
     * 反馈标题
     */
    private String title;
    /**
     * 反馈内容
     */
    private String content;
    /**
     * 反馈时间
     */
    private Date feedBackTime;
    /**
     * 作者
     */
    private String userid;
}

