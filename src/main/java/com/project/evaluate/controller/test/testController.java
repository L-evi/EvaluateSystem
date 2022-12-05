package com.project.evaluate.controller.test;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/test")
public class testController {
    @RequestMapping(value = "/hello", method = RequestMethod.POST)
    public Map<String, Object> testHelloWorld(@RequestBody Map<String, Object> getMessage) {
        Map<String, Object> res = new HashMap<>();
//        打印出请求数据
        System.out.println(getMessage);
//        将getMessage中的所有内容加入到res中
        getMessage.forEach((key, value) -> res.putIfAbsent(key, value));
        res.put("mag", "Hello World!");
        return res;
    }

    @RequestMapping(value = "/helloget", method = RequestMethod.GET)
    public Map<String, Object> testGet() {
        Map<String, Object> res = new HashMap<>();
        res.put("msg", "Method GET OK!");
        return res;
    }
}
