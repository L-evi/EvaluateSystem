package com.project.evaluate.entity;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * (Bulletin)实体类
 *
 * @author makejava
 * @since 2023-01-10 11:17:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bulletin implements Serializable {
    private static final long serialVersionUID = -39987790457267163L;
    /**
     * ID
     */
    @JsonProperty("ID")
    private Integer ID;
    /**
     * 公告主题
     */
    @JsonProperty("subject")
    private String subject;
    /**
     * 公告内容
     */
    @JsonProperty("content")
    private String content;
    /**
     * 公告的时间
     */
    @JsonProperty("createTime")
    private Date createTime;
    /**
     * 显示的时间
     */
    @JsonProperty("issueTime")
    private Date issueTime;
    /**
     * 过期的时间
     */
    @JsonProperty("expiredTime")
    private Date expiredTime;
    /**
     * 公告人
     */
    @JsonProperty("operator")
    private String operator;
}

