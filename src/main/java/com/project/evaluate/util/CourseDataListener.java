package com.project.evaluate.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.project.evaluate.dao.CourseDao;
import com.project.evaluate.entity.Course;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/2/17 13:21
 */
@Slf4j
public class CourseDataListener implements ReadListener<Course> {

    @Resource
    private SqlSessionTemplate sqlSessionTemplate;

    private static final int BATCH_COUNT = 100;

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
        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
//        获取Dao
        CourseDao courseDao = session.getMapper(CourseDao.class);
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
            }
        } catch (Exception e) {
//            异常回滚
            session.rollback();
        } finally {
//            关闭session
            session.close();
        }
        log.info("存储数据库完成！");
    }
}