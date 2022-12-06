package com.project.evaluate;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.mapper.UserMapper;
import com.project.evaluate.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class EvaluateApplicationTests {

    @Test
    void contextLoads() {
    }

    /**
     * @param null
     * @return
     * @description 测试JwtUtil功能
     * @author Levi
     * @since 2022/12/5 17:04
     */
    @Test
    void testJwtUtil() throws Exception {
        String str = "JwtUtil";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("str", str);
        String token = JwtUtil.createJwt(String.valueOf(jsonObject));
        System.out.println(token);
        Claims claims = JwtUtil.parseJwt(token);
        jsonObject = JSONObject.parseObject(claims.getSubject());
        System.out.println(jsonObject);
        System.out.println(claims.getExpiration());
    }

    @Autowired
    private UserMapper userMapper;

    /**
     * @param :
     * @return
     * @description : 测试Mybatis
     * @author Levi
     * @since 2022/12/6 10:13
     */
    @Test
    void testMybatis() throws IOException {
        System.out.println(userMapper.selectByUsername("20202132030").toString());
    }
}
