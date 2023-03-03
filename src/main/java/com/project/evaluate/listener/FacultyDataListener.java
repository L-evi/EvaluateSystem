package com.project.evaluate.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.project.evaluate.dao.FacultyDao;
import com.project.evaluate.entity.Faculty;
import com.project.evaluate.util.ApplicationContextProvider;
import com.project.evaluate.util.redis.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @description
 * @since 2023/3/3
 */
@Slf4j
public class FacultyDataListener implements ReadListener<Faculty> {

    /**
     * 提交条数
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 临时数据数组
     */
    private List<Faculty> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    public FacultyDataListener() {
    }

    @Override
    public void invoke(Faculty faculty, AnalysisContext context) {
        log.info("解析到一条数据：{}", JSON.toJSONString(faculty));
        /*
            设置密码
         */
        faculty.setPassword(new Md5Hash(faculty.getUserID(), faculty.getUserID(), 1024).toHex());
        cachedDataList.add(faculty);
        /*
          达到BATCH_COUNT就提交存储一次
         */
        if (cachedDataList.size() == BATCH_COUNT) {
            saveData();
            /*
             * 清理List
             */
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("所有数据解析完成！");
    }

    private void saveData() {
        log.info("{}条数据，开始存储数据", cachedDataList.size());
        /*
         * 自动提交设置为false，手动控制提交条数，模式为BATCH
         */
        SqlSessionTemplate sqlSessionTemplate = ApplicationContextProvider.getApplicationContext().getBean(SqlSessionTemplate.class);
        SqlSession sqlSession = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        /*
         * 获取dao
         */
        FacultyDao facultyDao = sqlSession.getMapper(FacultyDao.class);
        /*
         * 获取redisCache
         */
        RedisCache redisCache = ApplicationContextProvider.getApplicationContext().getBean(RedisCache.class);
        /*
         * 开始存储数据
         */
        int size = cachedDataList.size();
        int batchCount = 10;
        int count = size % batchCount == 0 ? size / batchCount : size / batchCount + 1;
        try {
            for (int i = 0; i < count; i++) {
                int startIndex = i * batchCount;
                /*
                 * 超过了界限则说明要去到边界了
                 */
                int endIndex = Math.min((i + 1) * batchCount, size);
                /*
                 * 提交并清理缓存
                 */
                List<Faculty> faculties = cachedDataList.subList(startIndex, endIndex);
                facultyDao.insertPageFaculty(faculties);
                sqlSession.commit();
                sqlSession.clearCache();
                log.info("第{}次提交", i + 1);
                /*
                 * 存储到redis中
                 */
                if (Objects.nonNull(redisCache)) {
                    faculties.stream().forEach(obj -> {
                        obj.setPassword(null);
                        redisCache.setCacheObject("Faculty:" + obj.getUserID(), obj, 1, TimeUnit.DAYS);
                    });
                }
            }
        } finally {
            /*
             * 关闭session
             */
            sqlSession.clearCache();
            sqlSession.close();
        }
        log.info("存储数据库完成");
    }
}
