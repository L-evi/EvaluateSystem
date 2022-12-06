package com.project.evaluate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class EvaluateApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvaluateApplication.class, args);
    }

}
