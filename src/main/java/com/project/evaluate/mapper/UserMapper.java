package com.project.evaluate.mapper;

import com.project.evaluate.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    User selectByUsername(String username);
}
