package com.project.evaluate.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import org.springframework.stereotype.Component;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2023/2/26 01:49
 */
@Component
public class CourseCourseTypeConverter implements Converter<Integer> {
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
        switch (value) {
            case "理论课程":
                return 0;
            case "实验课程":
                return 1;
            case "课程项目":
                return 2;
            default:
                // 找不到指定的课程类型返回-1
                return -1;
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) throws Exception {
        Integer value = context.getValue();
        switch (value) {
            case 0:
                return new WriteCellData<>("理论课程");
            case 1:
                return new WriteCellData<>("实验课程");
            case 2:
                return new WriteCellData<>("课程项目");
            default:
                return new WriteCellData<>("未知");
        }
    }
}
