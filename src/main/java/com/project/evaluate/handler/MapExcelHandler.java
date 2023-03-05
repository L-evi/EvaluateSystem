package com.project.evaluate.handler;

import com.alibaba.excel.util.ListUtils;

import java.util.*;

/**
 * @author Levi
 * @description 将map转化为excel的工具类
 * @since 2023/3/5 15:19
 */
public class MapExcelHandler {

    private static Map<String, String> COLUMN_NAME_MAP;

    /**
     * @param map 传入的map
     * @return Excel的表头
     * @description 将传入的map中的key转化为excel的表头
     * @author Levi
     */
    public static List<List<String>> getExcelHead(Map<String, Object> map) {
        if (Objects.isNull(map) || map.isEmpty()) {
            throw new RuntimeException(NullPointerException.class.getName() + " : 表头不能为空");
        }
        List<List<String>> head = ListUtils.newArrayList();
        Set<String> keySet = map.keySet();
        keySet.stream().forEach(obj -> {
            List<String> list = ListUtils.newArrayList();
            list.add(obj);
            head.add(list);
        });
        return head;
    }

    /**
     * @param map
     * @param nameMap
     * @return
     * @description 将传入的map中的key按照nameMap中的规则转化为excel的表头，其中nameMap：<key，COLUMN_NAME>
     * @author Levi
     */
    public static List<List<String>> getExcelHead(Map<String, Object> map, Map<String, String> nameMap) {
        if (Objects.isNull(nameMap) || nameMap.isEmpty()) {
            getExcelHead(map);
        }
        COLUMN_NAME_MAP = nameMap;
        if (Objects.isNull(map) || map.isEmpty()) {
            throw new RuntimeException(NullPointerException.class.getName() + " : 表头不能为空");
        }
        List<List<String>> head = ListUtils.newArrayList();
        Set<String> keySet = map.keySet();
        keySet.stream().forEach(obj -> {
            System.out.println(obj);
            List<String> list = ListUtils.newArrayList();
            list.add(COLUMN_NAME_MAP.get(obj));
            head.add(list);
        });
        return head;
    }

    /**
     * @param mapList 传入的map数据
     * @return Excel的数据
     * @description 将map类型的数据转化为excel的数据
     * @author Levi
     */
    public static List<List<Object>> getExcelData(List<Map<String, Object>> mapList) {
        if (Objects.isNull(mapList) || mapList.isEmpty()) {
            throw new RuntimeException(NullPointerException.class.getName() + " : 数据集不能为空");
        }
        List<List<Object>> dataList = ListUtils.newArrayList();
        mapList.stream().forEach(obj -> {
            List<Object> list = ListUtils.newArrayList();
            Set<String> keySet = obj.keySet();
            keySet.stream().forEach(key -> {
                list.add(obj.get(key));
            });
            dataList.add(list);
        });
        return dataList;
    }


}
