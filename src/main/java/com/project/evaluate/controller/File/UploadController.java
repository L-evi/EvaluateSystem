package com.project.evaluate.controller.File;

import com.project.evaluate.util.response.ResponseResult;
import com.project.evaluate.util.response.ResultCode;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/16 15:22
 */
@RequestMapping("/api/common")
@Controller
@CrossOrigin("*")
@PropertySource("classpath:application.yml")
class UploadController {
    //    编码格式
    @Value("${file.character-set}")
    private static String character;
    //    文件前缀
    @Value("${file.pre-path}")
    private static String prePath;
    //    缓冲区大小阈值 TODO 无法读取到
    @Value("${file.threshold-size")
    private static int sizeThreshold;

    //    文件分片最大值
    @Value("${file.file-size-max}")
    private static Long fileSizeMax;

    //
    @Value("${file.request-size-max}")
    private static Long requestSizeMax;

    @RequestMapping(value = "/upload")

    public ResponseResult upload(HttpServletResponse response, HttpServletRequest request) {
//        设置编码格式
        response.setCharacterEncoding(character);
//        初始化变量
        Integer schunk = null; // 当前分片编号
        Integer schunks = null; // 总分片数
        String name = null; // 文件名
        String filePath = prePath; // 文件前缀路径
        BufferedOutputStream os = null; // 输出流
        try {
//            用于处理接受到的文件类
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(sizeThreshold); // 文件缓冲区大小
            factory.setRepository(new File(filePath)); // 设置文件缓冲区路径
//            解析request中的文件信息
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(fileSizeMax);
            upload.setSizeMax(requestSizeMax);
//            解析这个文件
            List<FileItem> items = upload.parseRequest(request);
//            取出文件信息
            for (FileItem item : items) {
                if (item.isFormField()) {
//                    获取当前分片序号
                    if ("chunk".equals(item.getFieldName())) {
                        schunk = Integer.parseInt(item.getString(character));
                    }
//                    获取总分片数
                    if ("chunks".equals(item.getFieldName())) {
                        schunks = Integer.parseInt(item.getString(character));
                    }
//                    获取文件名
                    if ("name".equals(item.getFieldName())) {
                        name = item.getString(character);
                    }
                }
            }
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
                        File file = new File(prePath, tempFileName);
//                        如果文件不存在则需要存下来
                        if (!file.exists()) {
                            item.write(file);
                        }
                    }
                }
            }
//            合并文件：有分片并且已经到了最后一个分片才需要合并
            if (schunks != null && schunk != null && schunk.intValue() == schunks.intValue() - 1) {
//                合并文件之后的路径
                File tempFile = new File(prePath, name);
                os = new BufferedOutputStream(new FileOutputStream(tempFile));
//                找出所有的分片
                for (int i = 0; i < schunks.intValue(); i++) {
                    File file = new File(prePath, i + '_' + name);
                    while (!file.exists()) {
//                        TODO 一直找不到文件怎么办
                        Thread.sleep(100);
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
            }
//            返回成功信息
            response.getWriter().write("上传成功" + name);
        } catch (Exception e) {
            System.out.println("upload模块 失败");
            throw new RuntimeException(e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    System.out.println("upload 模块 os 关闭失败");
                    throw new RuntimeException(e);
                }
            }
        }
        return new ResponseResult(ResultCode.SUCCESS);
    }

}
