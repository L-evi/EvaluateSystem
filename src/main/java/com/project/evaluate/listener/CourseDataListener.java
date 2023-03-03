package com.project.evaluate.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.project.evaluate.dao.CourseDao;
import com.project.evaluate.entity.Course;
import com.project.evaluate.util.ApplicationContextProvider;
import com.project.evaluate.util.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/2/17 13:21
 */
@Slf4j
public class CourseDataListener implements ReadListener<Course> {

    /**
     * 提交条数
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 数据List
     */
    private List<Course> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);


    public CourseDataListener() {
    }

    @Override
    public void invoke(Course course, AnalysisContext analysisContext) {
        log.info("解析到一条数据：{}", JSON.toJSONString(course));
        cachedDataList.add(course);
        // 达到BATCH_COUNT了，就要存储一次数据库，防止OOM
        if (cachedDataList.size() == BATCH_COUNT) {
            saveData();
            // 清理List
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    /**
     * @param analysisContext:
     * @return
     * @description 所有数据解析完都会走这一个函数
     * @author Levi
     * @since 2023/2/18 18:33
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("所有数据解析完成");
    }


    /**
     * @param :
     * @return
     * @description 存储数据库
     * @author Levi
     * @since 2023/2/18 18:35
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库", cachedDataList.size());
//        自动提交设置为false，自定义提交条数防止OOM，模式为BATCH
        SqlSessionTemplate sqlSessionTemplate = ApplicationContextProvider.getApplicationContext().getBean(SqlSessionTemplate.class);
        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
//        获取Dao
        CourseDao courseDao = session.getMapper(CourseDao.class);
        /*
         * 获取Redis
         */
        RedisCache redisCache = ApplicationContextProvider.getApplicationContext().getBean(RedisCache.class);
        int size = cachedDataList.size();
        int batchCount = 10;
        int count = size % batchCount == 0 ? size / batchCount : size / batchCount + 1;
        try {
            for (int i = 0; i < count; i++) {
                int fromIndex = i * batchCount;
                int toIndex = (i + 1) * batchCount;
                // 超过了界限就等于界限好了
                if (toIndex > size) {
                    toIndex = size;
                }
                List<Course> batchList = cachedDataList.subList(fromIndex, toIndex);
                courseDao.insertPageCourse(batchList);
                session.commit();
                session.clearCache();
                log.info("第{}次提交", i + 1);
                /*
                 * 数据存入到redis中
                 */
                if (Objects.nonNull(redisCache)) {
                    batchList.stream().forEach(obj -> {
                        redisCache.setCacheObject("Course:" + obj.getID(), obj, 1, TimeUnit.DAYS);
                    });
                }
            }
        } catch (Exception e) {
//            异常回滚
            session.rollback();
            throw new RuntimeException(e);
        } finally {
//            关闭session
            session.clearCache();
            session.close();
        }
        log.info("存储数据库完成！");
    }
}
