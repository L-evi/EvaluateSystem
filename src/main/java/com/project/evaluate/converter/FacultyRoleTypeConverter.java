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
public class FacultyRoleTypeConverter implements Converter<Integer> {
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
            case "系统管理员":
                return 0;
            case "教师":
                return 1;
            case "文档管理员":
                return 2;
            case "认证专家":
                return 3;
            default:
                return -1;
        }
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) throws Exception {
        Integer value = context.getValue();
        switch (value) {
            case 0:
                return new WriteCellData<>("系统管理员");
            case 1:
                return new WriteCellData<>("教师");
            case 2:
                return new WriteCellData<>("文档管理员");
            case 3:
                return new WriteCellData<>("认证专家");
            default:
                return new WriteCellData<>("未知人员");
        }
    }
}
