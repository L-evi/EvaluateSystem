package com.project.evaluate;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.mapper.FacultyMapper;
import com.project.evaluate.util.JwtUtil;
import io.jsonwebtoken.Claims;;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@SpringBootTest
@PropertySource("classpath:application.yml")
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
    private FacultyMapper facultyMapper;


    @Test
    void testMD5() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String string = "admin";
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println(encoder.encodeToString(messageDigest.digest(string.getBytes("utf-8"))));
        System.out.println(encoder.encodeToString(messageDigest.digest(string.getBytes("utf-8"))));
    }


    @Autowired
    private Environment environment;

    @Value("${file.pre-path}")
    private String path;

    @Test
    void testValue() {

        System.out.println(path);
    }

}

