package com.project.evaluate;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

}
