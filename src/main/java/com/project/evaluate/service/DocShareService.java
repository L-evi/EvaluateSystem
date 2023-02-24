package com.project.evaluate.service;

import com.project.evaluate.entity.DO.DocShareDO;
import com.project.evaluate.util.response.ResponseResult;

public interface DocShareService {

    ResponseResult addDocShare(DocShareDO docShareDO);

    ResponseResult selectDocShareByID(Integer ID);

    ResponseResult selectPageDocShare(DocShareDO docShareDO, Integer page, Integer pageSize, String orderBy);

    ResponseResult updateDocShare(DocShareDO docShareDO, String token);

    ResponseResult deleteDocShare(Integer ID, String token);

    ResponseResult submitDocument(DocShareDO docShareDO);
}
