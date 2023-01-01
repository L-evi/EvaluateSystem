package com.project.evaluate.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.util.KaptchaUtil;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 11:15
 */

@RestController
@RequestMapping(value = "/api/verify")
@CrossOrigin(value = "*")
public class VerifyCodeController {
    @Resource
    private RedisCache redisCache;

    @RequestMapping(value = "/get/code", method = RequestMethod.POST)
    public void getVerifyCode(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> dataMap) {
        int len = Integer.parseInt((String) dataMap.get("length"));
        String weight = (String) dataMap.get("weight");
        String height = (String) dataMap.get("height");
        String text = KaptchaUtil.getRandomText(len);
        try {
            BufferedImage bufferedImage = KaptchaUtil.getVertifyImage(Integer.parseInt(weight), Integer.parseInt(height), text);
            response.setContentType("image/jpeg");
            String uuid = String.valueOf(UUID.randomUUID()).replaceAll("-", "");
            response.setHeader("uuid", uuid);
//            存入redis中：60秒过期
            redisCache.setCacheObject("uuid:" + uuid, text, 60, TimeUnit.SECONDS);
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            System.out.println("验证码生成失败");
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/check/code", method = RequestMethod.POST)
    public ResponseResult checkVerifyCode(HttpServletRequest request) {
        String uuid = request.getHeader("uuid");
        String code = request.getHeader("code").toLowerCase(Locale.ROOT);
        JSONObject jsonObject = new JSONObject();
        if (!Strings.hasText(uuid)) {
            jsonObject.put("msg", "验证失败，缺乏UUID");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        if (!Strings.hasText(code)) {
            jsonObject.put("msg", "验证失败，缺乏验证码");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        String codeText = redisCache.getCacheObject("uuid:" + uuid);
        if (Strings.hasText(codeText) && codeText.toLowerCase(Locale.ROOT).equals(code)) {
            jsonObject.put("msg", "验证成功");
            return new ResponseResult(ResultCode.SUCCESS, jsonObject);
        } else {
            jsonObject.put("msg", "验证失败，验证码错误");
            return new ResponseResult(ResultCode.INVALID_PARAMETER, jsonObject);
        }
    }
}
