package com.project.evaluate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.mapper.FacultyMapper;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.redis.RedisCache;
import io.jsonwebtoken.Claims;;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
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
     * @param
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

    @Test
    void testClassToJSONObject() {
        Faculty faculty = new Faculty();
        faculty.setUserID("user");
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(faculty));
        System.out.println(jsonObject);
    }


    //    编码格式
    @Value("${file.character-set}")
    private String character;
    //    文件前缀
    @Value("${file.pre-path}")
    private static String prePath;
    //    缓冲区大小阈值 TODO 无法读取到
    @Value("${file.threshold-size")
    private String sizeThreshold;

    //    文件分片最大值
    @Value("${file.file-size-max}")
    private String fileSizeMax;

    //
    @Value("${file.request-size-max}")
    private String requestSizeMax;

    @Test
    void testGetValue() {
        System.out.println("character:" + character);
        System.out.println(sizeThreshold);
        System.out.println("file-size-max:" + Long.parseLong(fileSizeMax));
        System.out.println("request-size-max:" + Long.parseLong(requestSizeMax));
    }

    //    测试redis
    @Resource
    private RedisCache redisCache;

    @Test
    public void testRedis() {
        redisCache.setCacheObject("hello", "你好");
        String hello = redisCache.getCacheObject("hello");
        System.out.println(hello);
    }
}

