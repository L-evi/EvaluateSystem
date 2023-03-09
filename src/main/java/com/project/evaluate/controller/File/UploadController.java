package com.project.evaluate.controller.File;


import com.alibaba.fastjson.JSONObject;
import com.project.evaluate.annotation.RateLimiter;
import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;
import io.jsonwebtoken.lang.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Objects;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description 文件上传模块，必须保证servlet.multipart.enabled=false，否则无法获取到文件域
 * @since 2022/12/16 15:22
 */
@RequestMapping("/api/common/file")
@Controller
//@CrossOrigin("*")
@PropertySource("classpath:application.yml")
@Slf4j
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

    @RequestMapping(value = "/upload/page")
    @ResponseBody
//    @RateLimiter(value = 100, timeout = 1000)
    public ResponseResult upload(HttpServletResponse response, HttpServletRequest request) throws UnsupportedEncodingException {
        JSONObject jsonObject = new JSONObject();
//        初始化参数
        this.init();
//        设置编码格式
        response.setCharacterEncoding(this.character);
        log.info("文件上传开始：初始化参数以及设置编码格式：{}", this.character);
//        初始化变量
        Integer schunk = null; // 当前分片编号
        Integer schunks = null; // 总分片数
        String filename = null; // 文件名
        String filePath = this.tempPrePath; // 文件前缀路径
        BufferedOutputStream os = null; // 输出流
        log.info("初始化变量：filaPath:{}", filePath);
        try {
//            用于处理接受到的文件类
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(Integer.parseInt(this.sizeThreshold)); // 文件缓冲区大小
            factory.setRepository(new File(filePath)); // 设置文件缓冲区路径
            log.info("处理接收到的文件类、设置文件缓冲区大小、设置文件缓冲区路径");
//            解析request中的文件信息
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(Long.parseLong(this.fileSizeMax));
            upload.setSizeMax(Long.parseLong(this.requestSizeMax));
            log.info("解析request中的文件信息，设置参数：fileSizeMax-{}，requestSizeMax-{}", fileSizeMax, requestSizeMax);
//            解析这个文件
            List<FileItem> items = upload.parseRequest(request);
//            取出文件信息
            log.info("---------------------------------------------------------------------------------------------");
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
                        filename = item.getString(this.character);
                    }
                    log.info("分片序号：{}，总分片数：{}，文件名：{}", schunk, schunk, filename);
                }
            }
            log.info("---------------------------------------------------------------------------------------------");
//            System.out.println("上传文件：文件解析完成");
//            取出文件
            for (FileItem item : items) {
                if (!item.isFormField()) {
//                    缓存文件名，如果没有分片，则缓存文件名就是文件名
                    String tempFileName = filename;
//                    如果文件名存在，且含有分片，则说明可以存储下来
                    if (filename != null) {
                        if (schunk != null) {
//                            缓存文件名字：分片序号_文件名
                            tempFileName = schunk + '_' + filename;
                        }
                        File file = new File(this.tempPrePath, tempFileName);
//                        如果文件不存在则需要存下来
                        if (!file.exists()) {
                            item.write(file);
                        }
                    }
                }
            }
            log.info("文件上传完成，开始合并文件");
            //            合并文件：有分片并且已经到了最后一个分片才需要合并 todo:没有分片的时候应该处理一下
            if (schunks != null && schunk.intValue() == schunks.intValue() - 1) {
//                合并文件之后的路径
                File tempFile = new File(filePath, filename);
                log.info("文件合并之后的路径：{}", tempFile.getAbsolutePath());
                os = new BufferedOutputStream(new FileOutputStream(tempFile));
//                是否能够找到分片文件的标记
                boolean isExist = true;
//                找出所有的分片
                for (int i = 0; i < schunks; i++) {
                    File file = new File(filePath, i + '_' + filename);
                    int j = 0;
                    while (!file.exists()) {
                        log.info("等待文件，第{}次等待", j);
                        Thread.sleep(1000);
//                        如果超过了60秒还没有找到那些分片，就跳出来，并且将前面所有的分片删除
                        if (j == 60) {
                            UploadController.deleteFile(i, filename, filePath);
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
                if (!isExist) {
//                    返回失败信息
                    log.info("上传失败");
                    jsonObject.put("msg", "file upload fail");
                    jsonObject.put("error", "文件上传失败，分片丢失");
                    return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
                } else {
                    log.info("上传成功：filename：{}", filename);
                    //                返回成功信息
                    jsonObject.put("msg", "file upload success");
                    jsonObject.put("filename", tempFile.getAbsoluteFile());
                    return new ResponseResult(ResultCode.SUCCESS, jsonObject);
                }
            } else {
                // 小文件上传完成
                if (Objects.nonNull(filename)) {
                    File file = new File(tempPrePath, filename);
                    if (file.exists()) {
                        jsonObject.put("msg", "上传成功");
                        jsonObject.put("filename", file.getAbsolutePath());
                        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("upload 模块 失败");
        } finally {
            /*
                关闭流
            */
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
//                    System.out.println("upload 模块 os 关闭失败");
                    throw new RuntimeException("upload 模块 os 关闭失败");
                }
            }
        }
        log.info("不可到达区域");
        jsonObject.put("msg", "file upload failed");
        jsonObject.put("filename", null);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }

    private void init() {
        //        初始化参数
        if (!Strings.hasText(this.character)) {
            this.character = "UTF-8";
        }
        if (!Strings.hasText(this.sizeThreshold)) {
            this.sizeThreshold = "1024";
        }
        if (!Strings.hasText(this.fileSizeMax)) {
            Long number = 10L * 1024L * 1024L * 1024L;
            this.fileSizeMax = String.valueOf(number);
        }
        if (!Strings.hasText(this.requestSizeMax)) {
            Long number = 4000L * 1024L * 1024L * 1024L;
            this.requestSizeMax = String.valueOf(number);
        }
        // 如果临时路径为空，则建立在运行文件下面的temp文件夹中
        if (!Strings.hasText(this.tempPrePath)) {
            this.tempPrePath = System.getProperty("user.dir") + File.separator + "temp";
        }
        File file = new File(tempPrePath);
        if (!file.exists()) {
            file.mkdirs();
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

    @PostMapping("/upload/single")
    @ResponseBody
    @RateLimiter(value = 10, timeout = 100)
    public ResponseResult uploadSingle(HttpServletResponse response, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        // 初始化参数
        init();
        response.setCharacterEncoding(this.character);
        String filePath = this.tempPrePath;
        // 存储：文件名-文件绝对路径
        JSONObject files = new JSONObject();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        try {
            // 给文件管理工厂设置缓冲区位置以及大小
            factory.setSizeThreshold(Integer.parseInt(this.sizeThreshold));
            factory.setRepository(new File(filePath));
            // 设置upload的单个文件大小、总请求大小
            ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
            servletFileUpload.setFileSizeMax(Long.parseLong(this.fileSizeMax));
            servletFileUpload.setSizeMax(Long.parseLong(this.requestSizeMax));
            // 获取表单
            List<FileItem> fileItems = servletFileUpload.parseRequest(request);
            for (FileItem item : fileItems) {
                // 复杂表单域说明上传的是文件，简单表单域说明是其他参数
                if (!item.isFormField()) {
                    String filename = item.getName();
                    File file = new File(this.tempPrePath, filename);
                    // 文件写入 todo：后续可以使用Hash减少文件重复上传的次数
                    item.write(file);
                    files.put(filename, file.getAbsolutePath());
                    // 删除临时文件
                    item.delete();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        jsonObject.put("msg", "上传成功");
        jsonObject.put("array", files);
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }
}
