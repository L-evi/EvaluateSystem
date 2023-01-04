package com.project.evaluate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.mapper.CourseDocDetailMapper;
import com.project.evaluate.mapper.CourseDocTaskMapper;
import com.project.evaluate.mapper.CourseMapper;
import com.project.evaluate.mapper.FacultyMapper;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.redis.RedisCache;
import io.jsonwebtoken.Claims;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

;

@SpringBootTest
@PropertySource("classpath:application.yml")
@ComponentScan("classpath:mapper/*.xml")
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

    @Resource
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

        System.out.println(this.path);
    }

    @Test
    void testClassToJSONObject() {
        Faculty faculty = new Faculty();
        faculty.setUserId("user");
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
        System.out.println("character:" + this.character);
        System.out.println(this.sizeThreshold);
        System.out.println("file-size-max:" + Long.parseLong(this.fileSizeMax));
        System.out.println("request-size-max:" + Long.parseLong(this.requestSizeMax));
    }

    //    测试redis
    @Resource
    private RedisCache redisCache;

    @Test
    public void testRedis() {
        Faculty faculty = new Faculty();
        faculty.setUserId("admin");
        faculty.setPassword("admin");
        this.redisCache.setCacheObject("admin", faculty, 1, TimeUnit.MINUTES);
//        Faculty admin = JSONObject.parseObject(redisCache.getCacheObject("admin"), Faculty.class);
        JSONObject jsonObject = this.redisCache.getCacheObject("admin");
        Faculty admin = JSONObject.toJavaObject(jsonObject, Faculty.class);
        System.out.println(admin);
        System.out.println(jsonObject.toString());
//        redisCache.setCacheObject("Faculty" + admin.getUserID(), admin);
//        Faculty faculty = redisCache.getCacheObject("Faculty" + admin.getUserID());
//        System.out.println(faculty);
    }

    @Test
    public void testMd5() {
        Md5Hash md5Hash = new Md5Hash("teacher", "teacher", 1024);
        System.out.println(md5Hash.toHex());
    }

    @Resource
    private CourseMapper courseMapper;

    @Test
    public void testCourseMapper() {
//        List<Course> pageCourse = this.courseMapper.getPageCourse(0, 2);
//        pageCourse.forEach(System.out::println);
        System.out.println(this.courseMapper.selectByCourseID("1"));
    }


    @Resource
    private CourseDocDetailMapper courseDocDetailMapper;

    @Test
    public void testCourseDocDetailMapper() {
//        System.out.println(this.courseDocDetailMapper.deleteByTaskID(2));
//        List<CourseDocDetail> details = this.courseDocDetailMapper.selectByTaskID(2);
//        details.forEach(System.out::println);
    }

    @Resource
    private CourseDocTaskMapper courseDocTaskMapper;

    @Test
    public void testCourseDocTaskMapper() {
        Map<String, Object> map = new HashMap<>();
        map.put("start", 0);
        map.put("end", 10);
        map.put("teacher", "teacher");
        map.put("schoolEndYear", 2);
        CourseDocTask courseDocTask = JSON.parseObject(JSONObject.toJSONString(map), CourseDocTask.class);
        System.out.println(courseDocTask.toString());
        System.out.println("-----------------");
        Map<String, Object> objectMap = JSON.parseObject(JSON.toJSONString(courseDocTask), Map.class);
        objectMap.put("index", 0);
        objectMap.put("pageSize", 3);
        System.out.println(objectMap.toString());
        System.out.println("---------");
        List<CourseDocTask> courseDocTasks = this.courseDocTaskMapper.screenTeacherCourseDocTask(objectMap);
        courseDocTasks.forEach(System.out::println);
    }
}

