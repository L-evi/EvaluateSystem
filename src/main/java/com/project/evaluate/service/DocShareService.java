package com.project.evaluate.service;

import com.project.evaluate.entity.DocShare;
import com.project.evaluate.util.response.ResponseResult;

public interface DocShareService {

    ResponseResult addDocShare(DocShare docShare);

    ResponseResult selectDocShareByID(Integer ID);

    ResponseResult selectPageDocShare(DocShare docShare, Integer page, Integer pageSize, String orderBy);

    ResponseResult updateDocShare(DocShare docShare, String token);

    ResponseResult deleteDocShare(Integer ID, String token);

    ResponseResult submitDocument(DocShare docShare);
}
