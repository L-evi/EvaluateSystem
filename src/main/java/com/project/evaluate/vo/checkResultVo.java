package com.project.evaluate.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description 检验返回给前端的vo
 * @since 2022/12/6 17:57
 */
@Data
public class checkResultVo {
    /**
     * 是否已上传
     */
    private Boolean uploaded;
    private String url;
    private List<Integer> uploadedChunks;
}
