package com.project.evaluate.controller.File;


import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.dao.CourseDao;
import com.project.evaluate.dao.CourseDocDetailDao;
import com.project.evaluate.dao.CourseDocTaskDao;
import com.project.evaluate.entity.Course;
import com.project.evaluate.entity.CourseDocDetail;
import com.project.evaluate.entity.CourseDocTask;
import com.project.evaluate.util.redis.RedisCache;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/16 15:22
 */
@RequestMapping("/api/common/file")
@ResponseBody
@Controller
@CrossOrigin("*")
@PropertySource("classpath:application.yml")
class UploadController {
    //    编码格式
    @Value("${file.character-set}")
    private String character;

    //    缓存文件前缀
    @Value("${file.temp-pre-path}")
    private String tempPrePath;

    //    缓冲区大小阈值
    @Value("${file.threshold-size}")
    private String sizeThreshold;

    //    文件分片最大值
    @Value("${file.file-size-max}")
    private String fileSizeMax;

    //      请求最大值
    @Value("${file.request-size-max}")
    private String requestSizeMax;

    @RequestMapping(value = "/upload")
    @ResponseBody
    public ResponseResult upload(HttpServletResponse response, HttpServletRequest request) {
//        System.out.println("文件上传开始");
//        初始化参数
        this.init();
//        设置编码格式
        response.setCharacterEncoding(this.character);
//        初始化变量
        Integer schunk = null; // 当前分片编号
        Integer schunks = null; // 总分片数
        String name = null; // 文件名
        String filePath = this.tempPrePath; // 文件前缀路径
        BufferedOutputStream os = null; // 输出流
        try {
//            用于处理接受到的文件类
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(Integer.parseInt(this.sizeThreshold)); // 文件缓冲区大小
            factory.setRepository(new File(filePath)); // 设置文件缓冲区路径
//            解析request中的文件信息
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(Long.parseLong(this.fileSizeMax));
            upload.setSizeMax(Long.parseLong(this.requestSizeMax));
//            解析这个文件
            List<FileItem> items = upload.parseRequest(request);
//            取出文件信息
            for (FileItem item : items) {
                if (item.isFormField()) {
//                    获取当前分片序号
                    if ("chunk".equals(item.getFieldName())) {
                        schunk = Integer.parseInt(item.getString(this.character));
                    }
//                    获取总分片数
                    if ("chunks".equals(item.getFieldName())) {
                        schunks = Integer.parseInt(item.getString(this.character));
                    }
//                    获取文件名
                    if ("name".equals(item.getFieldName())) {
                        name = item.getString(this.character);
                    }
                }
            }
//            System.out.println("上传文件：文件解析完成");
//            取出文件
            for (FileItem item : items) {
                if (!item.isFormField()) {
//                    缓存文件名，如果没有分片，则缓存文件名就是文件名
                    String tempFileName = name;
//                    如果文件名存在，且含有分片，则说明可以存储下来
                    if (name != null) {
                        if (schunk != null) {
//                            缓存文件名字：分片序号_文件名
                            tempFileName = schunk + '_' + name;
                        }
                        File file = new File(this.tempPrePath, tempFileName);
//                        如果文件不存在则需要存下来
                        if (!file.exists()) {
                            item.write(file);
                        }
                    }
                }
            }
//            System.out.println("上传文件：分片文件存储完成");
//            合并文件：有分片并且已经到了最后一个分片才需要合并
            if (schunks != null && schunk.intValue() == schunks.intValue() - 1) {
//                合并文件之后的路径
                File tempFile = new File(filePath, name);
                os = new BufferedOutputStream(new FileOutputStream(tempFile));
//                是否能够找到分片文件的标记
                boolean isExist = true;
//                找出所有的分片
                for (int i = 0; i < schunks; i++) {
                    File file = new File(filePath, i + '_' + name);
                    int j = 0;
                    while (!file.exists()) {
                        Thread.sleep(100);
//                        如果超过了一定时间还没有找到那些分片，就跳出来，并且将前面所有的分片删除
                        if (j == schunks) {
                            UploadController.deleteFile(i, name, filePath);
                            isExist = false;
                            break;
                        }
                        j++;
                    }
//                    如果读不到分片文件，则跳出循环
                    if (!isExist) {
                        break;
                    }
//                    将分片文件读取到byte数组中
                    byte[] bytes = FileUtils.readFileToByteArray(file);
//                    写入
                    os.write(bytes);
                    os.flush();
//                    删除临时文件
                    file.delete();
                }
                os.flush();
                if (isExist == false) {
//                    返回失败信息
                    response.setHeader("msg", "file upload fail");
                    response.setHeader("status", "0");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", "file upload fail");
                    jsonObject.put("error", "文件上传失败，分片丢失");
                    return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
                } else {
                    //                返回成功信息
                    response.setHeader("msg", "file upload success");
                    response.setHeader("filename", name);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", "file upload success");
                    jsonObject.put("filename", name);
                    return new ResponseResult(ResultCode.SUCCESS, jsonObject);
                }
            }
        } catch (Exception e) {
//            System.out.println("upload模块 失败");
            throw new RuntimeException("upload 模块 失败");
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
//                    System.out.println("upload 模块 os 关闭失败");
                    throw new RuntimeException("upload 模块 os 关闭失败");
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "file upload success");
        jsonObject.put("FileName", name);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    private void init() {
        //        初始化参数
        if (!Strings.hasText(this.character)) {
            this.character = "utf8";
        }
        if (!Strings.hasText(this.sizeThreshold)) {
            this.sizeThreshold = "1024";
        }
        if (!Strings.hasText(this.fileSizeMax)) {
            Long number = 5L * 1024L * 1024L * 1024L;
            this.fileSizeMax = String.valueOf(number);
        }
        if (!Strings.hasText(this.requestSizeMax)) {
            Long number = 10L * 1024L * 1024L * 1024L;
            this.requestSizeMax = String.valueOf(number);
        }
        if (!Strings.hasText(this.tempPrePath)) {
            this.tempPrePath = "~";
        }
    }

    private static void deleteFile(int index, String name, String filePath) {
        for (int i = 0; i < index; i++) {
            File file = new File(filePath, i + "_" + name);
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
