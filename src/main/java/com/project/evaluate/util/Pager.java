package com.project.evaluate.util;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Levi
 * @description
 * @since 2023/3/2
 */
public class Pager<T> {
    public PageInfo<T>  getListPage(List<T> list, Integer pageNum, Integer pageSize) {
        Page<T> page = new Page<>(pageNum, pageSize);
        Integer total = list.size();
        page.setTotal(total);
        Integer startIndex = (pageNum - 1) * pageSize;
        Integer endIndex = Math.min(startIndex + pageSize,total);
        if (startIndex > endIndex) {
            page.addAll(new ArrayList<T>());

        } else {
            page.addAll(list.subList(startIndex,endIndex));
        }
        return new PageInfo<T>(page);
    }
}
