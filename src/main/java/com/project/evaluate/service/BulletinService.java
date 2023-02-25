package com.project.evaluate.service;

import com.project.evaluate.entity.Bulletin;
import com.project.evaluate.util.response.ResponseResult;

public interface BulletinService {
    ResponseResult insertBulletin(Bulletin bulletin);

    ResponseResult selectPageBulletin(Bulletin bulletin, Integer role, Integer page, Integer pageSize, String orderBy);

    ResponseResult selectSingleBulletin(Integer ID);

    ResponseResult updateBulletin(Bulletin bulletin);

    ResponseResult deleteBulletin(Integer ID);
}
