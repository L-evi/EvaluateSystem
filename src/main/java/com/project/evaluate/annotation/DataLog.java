package com.project.evaluate.annotation;


import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DataLog {
    //    模块名
    String modelName() default "null";

    //    操作类型
    String operationType() default "null";
}
