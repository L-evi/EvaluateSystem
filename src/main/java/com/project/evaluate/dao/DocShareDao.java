package com.project.evaluate.dao;


import com.project.evaluate.entity.DO.DocShareDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DocShareDao {
    Long insertDocShare(DocShareDO docShareDO);

    List<Map<String, Object>> selectPageDocShare(DocShareDO docShareDO);

    DocShareDO selectDocShare(Integer ID);

    Long updateDocShare(DocShareDO docShareDO);

    Long deleteDocShare(Integer ID, String userID);
}
