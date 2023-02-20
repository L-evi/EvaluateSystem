package com.project.evaluate.service;

import com.project.evaluate.entity.DO.BulletinDO;
import com.project.evaluate.util.response.ResponseResult;

public interface BulletinService {
    ResponseResult insertBulletin(BulletinDO bulletinDO);

    ResponseResult selectPageBulletin(BulletinDO bulletinDO, Integer role, Integer page, Integer pageSize, String orderBy);

    ResponseResult selectSingleBulletin(Integer ID);

    ResponseResult updateBulletin(BulletinDO bulletinDO);

    ResponseResult deleteBulletin(Integer ID);
}
