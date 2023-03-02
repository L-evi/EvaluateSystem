package com.project.evaluate;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.evaluate.dao.*;
import com.project.evaluate.entity.*;
import com.project.evaluate.util.JwtUtil;
import com.project.evaluate.util.Pager;
import com.project.evaluate.util.bloom.BloomFilterHelper;
import com.project.evaluate.util.bloom.RedisBloomFilter;
import com.project.evaluate.util.redis.RedisCache;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest
@PropertySource("classpath:application.yml")
@ComponentScan("classpath:mapper/*.xml")
@Slf4j
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
    private FacultyDao facultyDao;

    @Test
    void testFacultyDao() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        this.facultyDao.selectPageFaculty(new Faculty()).forEach(System.out::println);
    }

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


    @Test
    void testValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("taskID", 2);
        String taskID = map.get("taskID").toString();
        System.out.println(taskID);
        Integer id = Integer.parseInt(taskID);
        System.out.println(id);
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


    //    文件分片最大值
    @Value("${file.file-size-max}")
    private String fileSizeMax;

    //
    @Value("${file.request-size-max}")
    private String requestSizeMax;

    @Test
    void testGetValue() {
        System.out.println("character:" + this.character);
//        System.out.println(this.sizeThreshold);
        System.out.println("file-size-max:" + Long.parseLong(this.fileSizeMax));
        System.out.println("request-size-max:" + Long.parseLong(this.requestSizeMax));
    }

    //    测试redis
    @Resource
    private RedisCache redisCache;

    @Test
    public void testRedis() {
        Faculty faculty = new Faculty();
        faculty.setUserID("admin");
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
        Md5Hash md5Hash = new Md5Hash("admin123", "admin", 1024);
        System.out.println(md5Hash.toHex());
    }

    @Resource
    private CourseDao courseDao;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Test
    public void testCourseMapper() {
//        List<Course> pageCourse = this.courseMapper.getPageCourse(0, 2);
//        pageCourse.forEach(System.out::println);
        List<Course> cours = courseDao.selectPageCourse(new Course());
        cours.forEach(System.out::println);
//        for (Course course : cours) {
//            Boolean aBoolean = courseDao.deletaByID(course.getID());
//            System.out.println(aBoolean);
//            Integer integer = courseDao.insertCourse(course);
//            System.out.println(integer);
//        }
//        新获取一个模式为BATCH，自动提交为false的session
//        如果自动提交设置为true,将无法控制提交的条数，改为最后统一提交，可能导致内存溢出
//        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
////        通过新的session获取mapper
//        CourseDao courseMapper = session.getMapper(CourseDao.class);
//        int size = cours.size();
//        try {
//            for (int i = 0; i < size; i++) {
//                courseMapper.insertCourse(cours.get(i));
//                if (i == size - 1) {
////                    手动每1000个一提交，提交后无法回滚
//                    session.commit();
//                    System.out.println("commit complete");
////                    清理缓存，防止溢出
//                    session.clearCache();
//                }
//            }
//        } catch (Exception e) {
////            没有提交的数据可以回滚
//            session.rollback();
//        } finally {
//            session.close();
//        }
    }


    @Resource
    private CourseDocDetailDao courseDocDetailDao;

    @Test
    public void testCourseDocDetailMapper() {
//        System.out.println(this.courseDocDetailMapper.deleteByTaskID(2));
//        List<CourseDocDetail> details = this.courseDocDetailMapper.selectByTaskID(2);
//        details.forEach(System.out::println);
        List<CourseDocDetail> courseDocDetails = this.courseDocDetailDao.selectByTaskID(2);
        courseDocDetails.forEach(System.out::println);
    }

    @Resource
    private CourseDocTaskDao courseDocTaskDao;

    @Test
    public void testCourseDocTaskMapper() {
//        Map<String, Object> map = new HashMap<>();
//        map.put("start", 0);
//        map.put("end", 10);
//        map.put("teacher", "teacher");
//        map.put("schoolEndYear", 2);
//        CourseDocTask courseDocTask = JSON.parseObject(JSONObject.toJSONString(map), CourseDocTask.class);
//        System.out.println(courseDocTask.toString());
//        System.out.println("-----------------");
//        Map<String, Object> objectMap = JSON.parseObject(JSON.toJSONString(courseDocTask), Map.class);
//        objectMap.put("index", 0);
//        objectMap.put("pageSize", 3);
//        System.out.println(objectMap.toString());
//        System.out.println("---------");
//        List<CourseDocTask> courseDocTasks = this.courseDocTaskDao.screenTeacherCourseDocTask(objectMap);
//        courseDocTasks.forEach(System.out::println);
       /* CourseDocTask courseDocTask = new CourseDocTask();
        courseDocTask.setID(3);
        PageHelper.startPage(0, 5, "ID DESC");
        List<Map<String, Object>> courseDocTasks = this.courseDocTaskDao.selectPageCourseDocTask(courseDocTask);
        PageInfo<Map<String, Object>> courseDocTaskPageInfo = new PageInfo<>(courseDocTasks);
        List<Map<String, Object>> list = courseDocTaskPageInfo.getList();
        list.forEach(System.out::println);*/
        List<Integer> ids = new ArrayList<>();
        ids.add(2);
        ids.add(3);
        List<CourseDocTask> courseDocTasks = courseDocTaskDao.selectPageID(ids);
//        Integer num = courseDocTaskDao.insertPageCourseDocTask(courseDocTasks);
//        log.info("{} 条数据被插入", num);

    }

    @Test
    public void testPageHelper() {
        PageHelper.startPage(0, 5);
        PageHelper.orderBy("taskID DESC");
        List<CourseDocDetail> all = this.courseDocDetailDao.getAll();
        PageInfo<CourseDocDetail> pageInfo = new PageInfo<CourseDocDetail>(all);
        List<CourseDocDetail> list = pageInfo.getList();
        list.forEach(System.out::println);
        long endRow = pageInfo.getEndRow();
        System.out.println("end row:" + endRow);
        System.out.println("pages:" + pageInfo.getPages());
    }

    @Resource
    private FeedbackDao feedbackDao;

    @Test
    public void testFeedbackDao() {
//        this.feedbackDao.insert(new Feedback(null, "反馈标题", "反馈内容：测试内容", new Date(), "teach"));
//        System.out.println(this.feedbackDao.selectByID(9));
        Feedback feedback = new Feedback();
        PageHelper.startPage(0, 15, "ID ASC");
        List<Feedback> feedbacks = this.feedbackDao.selectByFeedback(feedback);
        PageInfo<Feedback> pageInfo = new PageInfo<>(feedbacks);
//        System.out.println(pageInfo.getNextPage());
//        pageInfo.calcByNavigatePages(2);
        pageInfo.getList().forEach(System.out::println);
        System.out.println(pageInfo.getPages());
        System.out.println(pageInfo.getTotal());
//        System.out.println(feedbacks.toString());
//        System.out.println(this.feedbackDao.delete(1));
    }

    @Resource
    private BulletinDao bulletinDao;

    @Test
    public void testBulletinDao() {
//        Bulletin bulletin = this.bulletinDao.selectByID(2);
//        System.out.println(bulletin.toString());
//        bulletin.setID(null);
//        Long aLong = this.bulletinDao.insertBulletin(bulletin);
//        System.out.println(aLong);
//        Boolean aBoolean = this.bulletinDao.deleteByID(1);
//        System.out.println(aBoolean);
        List<Bulletin> bulletins = this.bulletinDao.selectByBulletin(new Bulletin(), null);
        bulletins.forEach(System.out::println);
    }

    @Resource
    private DocShareDao docShareDao;

    @Test
    public void testDocShareDao() {
//        DocShare docShare = new DocShare();
//        docShare.setTitle("文档分享标题2");
//        docShare.setDesc("文档分享描述2");
//        docShare.setSubmitter("admin");
//        docShare.setDocPath("C:\\Users\\zwk57\\IdeaProjects\\EvaluateSystem\\文档\\计算机学院本科教学文档管理系统需求说明书 v0.5(1).doc");
//        docShare.setDocSize(3);
//        docShare.setUploadTime(new Date());
//        插入
//        System.out.println(docShareDao.insertDocShare(docShare));
//        根据ID查询
//        System.out.println(this.docShareDao.selectDocShare(2).toString());
//        分页查询
        PageHelper.startPage(0, 2);
        PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(this.docShareDao.selectPageDocShare(new DocShare()));
        pageInfo.getList().forEach(System.out::println);
//        修改
//        docShare.setID(3);
//        docShare.setTitle("修改标题");
//        docShare.setDesc("修改描述");
//        System.out.println(this.docShareDao.updateDocShare(docShare));
//        删除
//        System.out.println(docShareDao.deleteDocShare(3));
    }

    @Resource
    private SyslogDao syslogDao;

    @Test
    public void testSyslog() {
        Syslog syslog = new Syslog();
        syslog.setOperator("admin");
        syslog.setConditions("test conditions");
        syslog.setModule("test module");
        syslog.setAction("test action");
        syslog.setLogTime(new Date());
        syslog.setResult("test result");
        syslog.setStatus(1);
//        System.out.println(this.syslogDao.insertSyslog(syslog));

//        System.out.println(this.syslogDao.selectByID(1));
        System.out.println("-------------------------");
        PageHelper.startPage(0, 2);
//        PageInfo<Syslog> pageInfo = new PageInfo<>(this.syslogDao.selectPageSysLog(syslog, null, null));
//        pageInfo.getList().forEach(System.out::println);
//        System.out.println(this.syslogDao.deleteSyslogByID(1));
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(2);
        list.add(5);
//        System.out.println(this.syslogDao.deletePageSyslog(list));
    }

    @Autowired
    private RedisBloomFilter redisBloomFilter;

    @Autowired
    private BloomFilterHelper bloomFilterHelper;

    @Test
    public void redisBloomFilter() {
        List<String> allResourceId = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            allResourceId.add(String.valueOf(i));
        }
        for (String id : allResourceId) {
            //将所有的资源id放入到布隆过滤器中
            redisBloomFilter.addByBloomFilter(bloomFilterHelper, "bloom", id);
        }
//        数据加入完成
        boolean mightContain = redisBloomFilter.includeByBloomFilter(bloomFilterHelper, "bloom", "2");
        if (!mightContain) {
            System.out.println("数据不存在");
        } else {
            System.out.println("数据存在");
        }
    }

    @Test
    public void importCourseExcel() {
        // 写法1：JDK8+ ,不用额外写一个DemoDataListener
        // since: 3.0.0-beta1
//        String fileName = "";
        // 这里默认每次会读取100条数据 然后返回过来 直接调用使用数据就行
        // 具体需要返回多少行可以在`PageReadListener`的构造函数设置
/*        EasyExcel.read(fileName, Course.class, new PageReadListener<Course>(dataList -> {
            for (Course course : dataList) {
                log.info("读取到一条数据{}", JSON.toJSONString(course));
            }
        })).sheet().doRead();*/
    }

    @Test
    public void testGenerateExcelTemplate() {
        String fileName = "/Users/apple/Documents/Program/Java/EvaluateSystem/src/main/resources/static" + File.separator + System.currentTimeMillis() + ".xlsx";
        List<Course> courses = courseDao.selectPageCourse(new Course());
        EasyExcel.write(fileName, Course.class).sheet("course").doWrite(courses);
    }

    @Test
    public void testInsertPageCourse() {
        List<Course> list = courseDao.selectPageCourse(new Course());
        list.forEach(System.out::println);
 /*       Integer integer = courseDao.insertPageCourse(list);
        log.info("插入了{}条数据",integer);*/
    }

    @Test
    public void testRedisList() {
        List<Course> list = courseDao.selectPageCourse(new Course());
//        list.forEach(System.out::println);
        redisCache.deleteObject("Courses");
        redisCache.setCacheList("Courses", list);
        List<Object> courses = redisCache.getCacheList("Courses");
        courses.forEach((course) -> {
            Course course_one = JSON.parseObject(JSON.toJSONString(course), Course.class);
            System.out.println(course_one.toString());
        });
    }

    @Test
    public void testPageHelperList() {
        List<Course> courses = redisCache.getCacheList("Course:21HA2560").stream().map(obj -> JSON.parseObject(JSON.toJSONString(obj), Course.class)).collect(Collectors.toList());
        Pager<Course> pager = new Pager<>();
        PageInfo<Course> listPage = pager.getListPage(courses, 1, 1);
        listPage.getList().stream().forEach(System.out::println);
        System.out.println(listPage.getTotal());
        System.out.println(listPage.getPages());
    }
}

