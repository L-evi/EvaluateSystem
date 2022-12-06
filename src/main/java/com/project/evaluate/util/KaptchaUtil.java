package com.project.evaluate.util;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Configuration;

import java.awt.image.BufferedImage;
import java.util.Properties;
import java.util.Random;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/6 10:52
 */
@Configuration
public class KaptchaUtil {
    //    验证码所含的字符
    private static final String CHAR_SET = "123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";

    /**
     * @param :
     * @return 返回RGB三原色的字符串
     * @description 获取随机的RGB三原色
     * @author Levi
     * @since 2022/12/6 11:13
     */
    private static String getRandomColor() {
        Random random = new Random();
        return random.nextInt(256) + "," + random.nextInt(256) + "," + random.nextInt(256);
    }

    /**
     * @param weight 验证码图片的长度
     * @param height 验证码图片的宽度
     * @param length 验证码的密文长度
     * @return 返回Producer类型的验证码生成工具
     * @description 设置验证码的相关属性
     * @author Levi
     * @since 2022/12/6 10:54
     */
    private static Producer producer(String weight, String height, String length) {
        Properties properties = new Properties();
        //设置图片边框
        properties.setProperty("kaptcha.border", "no");
        //设置图片边框为蓝色
        //properties.setProperty("kaptcha.border.color", "white");
        // 背景颜色渐变开始
        properties.put("kaptcha.background.clear.from", getRandomColor());
        // 背景颜色渐变结束
        properties.put("kaptcha.background.clear.to", getRandomColor());
        // 字体颜色：随机颜色
        properties.put("kaptcha.textproducer.font.color", getRandomColor());
        // 文字间隔
        properties.put("kaptcha.textproducer.char.space", "10");
        // 干扰线颜色配置
        properties.put("kaptcha.noise.color", getRandomColor());
        //如果需要去掉干扰线
        //properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise");
        // 字体
        properties.put("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        // 图片宽度
        properties.setProperty("kaptcha.image.width", weight);
        // 图片高度
        properties.setProperty("kaptcha.image.height", height);
        // 从哪些字符中产生
        properties.setProperty("kaptcha.textproducer.char.string", CHAR_SET);
        // 字符个数
        properties.setProperty("kaptcha.textproducer.char.length", length);
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        defaultKaptcha.setConfig(new Config(properties));
        return defaultKaptcha;
    }

    /**
     * @param len 生成验证码的长度
     * @return 返回验证码的文本
     * @description 生成指定长度的验证码的文本
     * @author Levi
     * @since 2022/12/6 11:06
     */
    public static String getRandomText(int len) {
        StringBuffer text = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < len; i++) {
            text.append(CHAR_SET.charAt(random.nextInt(CHAR_SET.length())));
        }
        return text.toString();
    }

    /**
     * @param weight 验证码图片长度
     * @param height 验证码图片宽度
     * @param text   验证码文本
     * @return 返回验证码图片的BufferImage
     * @description 通过长度、宽度生成指定文本的验证码图片
     * @author Levi
     * @since 2022/12/6 11:12
     */
    public static BufferedImage getVertifyImage(int weight, int height, String text) {
        Producer kaptchaProducer = producer(String.valueOf(weight), String.valueOf(height), String.valueOf(text.length()));
        return kaptchaProducer.createImage(text);
    }

}
