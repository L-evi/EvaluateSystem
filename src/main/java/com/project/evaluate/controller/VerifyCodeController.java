package com.project.evaluate.controller;

import com.project.evaluate.util.KaptchaUtil;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 11:15
 */

@RestController
@RequestMapping(value = "/verify")
@CrossOrigin(value = "*")
public class VerifyCodeController {
    @RequestMapping(value = "/code", method = RequestMethod.POST)
    public void getVerifyCode(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> dataMap) {
        int len = Integer.parseInt((String) dataMap.get("length"));
        String weight = (String) dataMap.get("weight");
        String height = (String) dataMap.get("height");
        String text = KaptchaUtil.getRandomText(len);
        try {
            BufferedImage bufferedImage = KaptchaUtil.getVertifyImage(Integer.parseInt(weight), Integer.parseInt(height), text);
            response.setContentType("image/jpeg");
            response.setHeader("verify_code", text);
            OutputStream outputStream = response.getOutputStream();
            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            System.out.println("验证码生成失败");
            e.printStackTrace();
        }
    }
}
