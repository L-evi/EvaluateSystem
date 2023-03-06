package com.project.evaluate.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author Levi
 * @description
 * @since 2023/3/6
 */
public class FileSizeFormatter {
    /**
     * @description 将文件大小按照0,000.00的格式转换，单位自适应
     * @param filesize 文件大小，初始单位为B
     * @return 返回自适应单位之后的文件大小的值以及单位
     */
    public static String formatFileSizeString(String filesize) {
        Long size = new Long(filesize);
        if (size <= 0) {
            return "0B";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + units[digitGroups];
    }


    /**
     * @param size     文件大小
     * @param original 原先的单位
     * @param target   需要转换的单位
     * @return 返回转换后的数值以及单位
     * @description 将文件大小转换为指定单位，精度更高
     */
    public static String formatFileSize(long size, MemoryUnit original, MemoryUnit target) {
        if (size <= 0) {
            return "0";
        }
        BigDecimal sizeBigDecimal = new BigDecimal(size);
        /*
          获取位置，求取相差几个数量级
         */
        int originalIndex = MemoryUnit.unitsArray.indexOf(original);
        int targetIndex = MemoryUnit.unitsArray.indexOf(target);
        if (originalIndex == targetIndex) {
            return new DecimalFormat("#,##0.#").format(sizeBigDecimal);
        }
        /*
          根据数量级求出目标单位值
         */
        int exponential = targetIndex - originalIndex;
        BigDecimal result = sizeBigDecimal.divide(BigDecimal.valueOf(Math.pow(1024.0, exponential)), 2, RoundingMode.HALF_UP);
        return new DecimalFormat("#,##0.#").format(result) + target.getUnit();
    }
}
