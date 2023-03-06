package com.project.evaluate.controller.File;


import com.alibaba.fastjson.JSONArray;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    @RateLimiter(value = 100, timeout = 1000)
    public ResponseResult upload(HttpServletResponse response, HttpServletRequest request) {
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
                    if ("filename".equals(item.getFieldName())) {
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
            //            合并文件：有分片并且已经到了最后一个分片才需要合并
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
                        Thread.sleep(100);
//                        如果超过了一定时间还没有找到那些分片，就跳出来，并且将前面所有的分片删除
                        if (j == schunks) {
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
                if (isExist == false) {
//                    返回失败信息
                    log.info("上传失败");
                    response.setHeader("msg", "file upload fail");
                    response.setHeader("status", "0");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", "file upload fail");
                    jsonObject.put("error", "文件上传失败，分片丢失");
                    return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
                } else {
                    log.info("上传成功：filename：{}", filename);
                    //                返回成功信息
                    response.setHeader("msg", "file upload success");
                    response.setHeader("filename", filename);
                    System.out.println("response filename : " + filename);
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("msg", "file upload success");
                    jsonObject.put("filename", filename);
                    return new ResponseResult(ResultCode.SUCCESS, jsonObject);
                }
            } else {
                log.info("不合并文件，文件未保存");
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msg", "file upload success");
        jsonObject.put("filename", filename);
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

    @PostMapping("/upload/single")
    @ResponseBody
    @RateLimiter(value = 10, timeout = 100)
    public ResponseResult uploadSingleFile(@RequestPart(value = "file", required = true) MultipartFile multipartFile, @RequestParam(value = "filename", required = false) String filename) {
        JSONObject jsonObject = new JSONObject();
        if (Objects.isNull(multipartFile) || multipartFile.isEmpty()) {
            jsonObject.put("msg", "文件为空");
            return new ResponseResult(ResultCode.MISSING_PATAMETER, jsonObject);
        }
        StringBuilder filePath = new StringBuilder(tempPrePath).append(File.separator);
        String originalFilename = multipartFile.getOriginalFilename();
        if (Strings.hasText(filename)) {
            String[] originalFilenames = originalFilename.split("\\.");
            filePath.append(filename).append(".").append(originalFilenames[originalFilenames.length - 1]);
        } else {
            filePath.append(originalFilename);
        }
        try {
            File file = new File(filePath.toString());
            if (!file.exists()) {
                file.mkdirs();
            }
            multipartFile.transferTo(file);
        } catch (IOException e) {
            jsonObject.put("msg", "文件上传失败");
            return new ResponseResult(ResultCode.IO_OPERATION_ERROR, jsonObject);
        }
        jsonObject.put("filaname", filePath);
        jsonObject.put("msg", "文件上传成功");
        return new ResponseResult(ResultCode.SUCCESS, jsonObject);
    }
}
