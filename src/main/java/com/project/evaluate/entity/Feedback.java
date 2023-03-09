package com.project.evaluate.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("ID")
    private Integer ID;
    /**
     * 反馈标题
     */
    @JsonProperty("title")
    private String title;
    /**
     * 反馈内容
     */
    @JsonProperty("content")
    private String content;
    /**
     * 反馈时间
     */
    @JsonProperty("feedBackTime")
    private Date feedBackTime;
    /**
     * 作者
     */
    @JsonProperty("userID")
    private String userID;
}

