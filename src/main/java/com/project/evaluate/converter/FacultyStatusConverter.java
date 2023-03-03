package com.project.evaluate.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import org.springframework.stereotype.Component;

/**
 * @author Levi
 * @description
 * @since 2023/3/3
 */
@Component
public class FacultyStatusConverter implements Converter<Integer> {
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
        /*
         * 默认启用
         */
        return 1;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) throws Exception {
        Integer value = context.getValue();
        switch (value) {
            case 0:
                return new WriteCellData<>("禁用");
            case 1:
                return new WriteCellData<>("启用");
            default:
                return new WriteCellData<>("未知");
        }
    }
}
