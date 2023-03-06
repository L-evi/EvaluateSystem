package com.project.evaluate.util;

import lombok.Getter;

import java.util.ArrayList;

/**
 * @author Levi
 * @description 存储单位枚举类
 * @since 2023/3/6
 */
@Getter
public enum MemoryUnit {
    /**
     *
     */
    B("B"),
    KB("KB"),
    MB("MB"),
    GB("GB"),
    TB("TB");
    public static ArrayList<MemoryUnit> unitsArray = new ArrayList<MemoryUnit>() {
        {
            add(B);
            add(KB);
            add(MB);
            add(GB);
            add(TB);
        }
    };


    //    构造函数及其变量
    private final String unit;

    MemoryUnit(String unit) {
        this.unit = unit;
    }
}

