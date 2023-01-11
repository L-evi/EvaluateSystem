package com.project.evaluate.dao;


import com.project.evaluate.entity.Bulletin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface BulletinDao {
    Bulletin selectByID(Integer ID);

    List<Bulletin> selectByBulletin(@Param("bulletin") Bulletin bulletin, @Param("date") Date date);

    Long updateBulletin(Bulletin bulletin);

    Long insertBulletin(Bulletin bulletin);

    Boolean deleteByID(Integer ID);

}
