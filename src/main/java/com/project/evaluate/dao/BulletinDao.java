package com.project.evaluate.dao;


import com.project.evaluate.entity.DO.BulletinDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface BulletinDao {
    BulletinDO selectByID(Integer ID);

    List<BulletinDO> selectByBulletin(@Param("bulletin") BulletinDO bulletinDO, @Param("date") Date date);

    Long updateBulletin(BulletinDO bulletinDO);

    Long insertBulletin(BulletinDO bulletinDO);

    Boolean deleteByID(Integer ID);

}
