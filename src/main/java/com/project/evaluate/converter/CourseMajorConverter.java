package com.project.evaluate.converter;


import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.dao.MajorDao;
import com.project.evaluate.entity.Major;
import com.project.evaluate.util.ApplicationContextProvider;
import com.project.evaluate.util.redis.RedisCache;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class CourseMajorConverter implements Converter<Integer> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) throws Exception {
        String value = context.getReadCellData().getStringValue();
        ApplicationContext applicationContext = ApplicationContextProvider.getApplicationContext();
        RedisCache redisCache = applicationContext.getBean(RedisCache.class);
        MajorDao majorDao = applicationContext.getBean(MajorDao.class);
        Major major = JSONObject.toJavaObject(redisCache.getCacheObject("major:" + value), Major.class);
        if (Objects.isNull(major) || Objects.isNull(major.getID())) {
            major = majorDao.selectByMajorName(value);
            if (Objects.isNull(major) || Objects.isNull(major.getID())) {
                throw new RuntimeException("找不到指定的专业: " + value);
            }
            redisCache.setCacheObject("major:" + value, major, 1, TimeUnit.DAYS);
            redisCache.setCacheObject("major:" + major.getID(), major, 1, TimeUnit.DAYS);
        }
        return major.getID();
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) throws Exception {
        RedisCache redisCache = ApplicationContextProvider.getApplicationContext().getBean(RedisCache.class);
        MajorDao majorDao = ApplicationContextProvider.getApplicationContext().getBean(MajorDao.class);
        Major major = JSONObject.toJavaObject(redisCache.getCacheObject("major:" + context.getValue()), Major.class);
        if (Objects.isNull(major) || !Strings.hasText(major.getMajorName())) {
            major = majorDao.selectByMajorID(context.getValue());
            if (Objects.isNull(major) || Objects.isNull(major.getMajorName())) {
                throw new RuntimeException("找不到指定的专业: " + context.getValue());
            }
            redisCache.setCacheObject("major:" + major.getMajorName(), major, 1, TimeUnit.DAYS);
            redisCache.setCacheObject("major:" + major.getID(), major, 1, TimeUnit.DAYS);
        }
        return new WriteCellData<>(major.getMajorName());
    }
}
