package com.project.evaluate.dao;

import com.project.evaluate.entity.Major;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author Levi
 * @description
 * @since 2023/2/26 02:35
 */
@Mapper
@Repository
public interface MajorDao {
    Integer insertMajor(Major major);

    Major selectByMajorName(String majorName);

    Major selectByMajorID(Integer majorID);
}
