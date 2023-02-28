package converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;

/**
 * @author Levi
 * @description
 * @since 2023/2/28 14:30
 */
public class SyslogStatusConverter implements Converter<Integer> {
    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /*
     * @param context:
     * @return
     * @description :   读取excel时，将excel中的数据转换制定数据
     * @author Levi
     * @since 2023/2/28
     */
    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) throws Exception {
        return Converter.super.convertToJavaData(context);
    }

    /*
     * @param context:
     * @return
     * @description : 写出excel时将转换为指定的数据
     * @author Levi
     * @since 2023/2/28
     */
    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) throws Exception {
        Integer value = context.getValue();
        if (value.equals(1)) {
            return new WriteCellData<>("成功");
        } else if (value.equals(0)) {
            return new WriteCellData<>("失败");
        }
        return Converter.super.convertToExcelData(context);
    }
}
