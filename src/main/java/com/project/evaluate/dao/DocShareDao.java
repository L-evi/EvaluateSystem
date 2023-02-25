package com.project.evaluate.dao;


import com.project.evaluate.entity.DocShare;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DocShareDao {
    Long insertDocShare(DocShare docShare);

    List<Map<String, Object>> selectPageDocShare(DocShare docShare);

    DocShare selectDocShare(Integer ID);

    Long updateDocShare(DocShare docShare);

    Long deleteDocShare(Integer ID, String userID);
}
