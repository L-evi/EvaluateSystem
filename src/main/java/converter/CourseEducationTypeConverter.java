package converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import org.springframework.stereotype.Component;

/**
 * @author Levi
 * @description
 * @since 2023/2/26 01:48
 */
@Component
public class CourseEducationTypeConverter implements Converter<Integer> {
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
            case "通识教育":
                return 0;
            case "大类教育":
                return 1;
            case "专业教育":
                return 2;
            case "师范教育":
                return 3;
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
                return new WriteCellData<>("通识教育");
            case 1:
                return new WriteCellData<>("大类教育");
            case 2:
                return new WriteCellData<>("专业教育");
            case 3:
                return new WriteCellData<>("师范教育");
            default:
                return new WriteCellData<>("未知");
        }
    }
}
