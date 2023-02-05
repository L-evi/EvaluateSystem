package com.project.evaluate;

import com.google.common.base.Charsets;
import com.google.common.hash.Funnel;
import com.project.evaluate.util.bloom.BloomFilterHelper;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class EvaluateApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(EvaluateApplication.class);
    }


    public static void main(String[] args) {
        SpringApplication.run(EvaluateApplication.class, args);
    }


    //  初始化布隆过滤器，放入到spring容器里面
    @Bean
    public BloomFilterHelper<String> initBloomFilterHelper() {
        return new BloomFilterHelper<>((Funnel<String>) (from, into) -> into.putString(from, Charsets.UTF_8).putString(from, Charsets.UTF_8), 1000000, 0.01);
    }

}
