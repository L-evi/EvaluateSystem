package com.project.evaluate.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.fastjson.JSON;
import com.project.evaluate.dao.CourseDao;
import com.project.evaluate.entity.AO.CourseAO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/2/17 13:21
 */
@Slf4j
public class CourseDataListener implements ReadListener<CourseAO> {


    private static final int BATCH_COUNT = 100;

    private List<CourseAO> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private CourseDao courseDao;

    public CourseDataListener(CourseDao courseDao) {
        this.courseDao = courseDao;
    }

    @Override
    public void invoke(CourseAO courseAO, AnalysisContext analysisContext) {
        log.info("解析到一条数据：{}", JSON.toJSONString(courseAO));
        cachedDataList.add(courseAO);
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
        
        log.info("存储数据库完成！");
    }
}
