package com.project.evaluate.controller.File;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/16 15:23
 */
@Controller
@CrossOrigin("*")
@PropertySource("classpath:application.yml")
@RequestMapping("/api/common/file")
public class DownloadController {
    @Value("${file.character-set}")
    private static String charaset;

    //    下载日志EXCEL
    @GetMapping(value = "/download")
//    @RequiresRoles(value = "1", logical = Logical.OR)
    public void downloadSyslog(String filename, HttpServletResponse response) {
        File file = new File(filename);
        if (file.exists()) {
//            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding(charaset);
            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(response.getOutputStream()); BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));) {
                // 这里URLEncoder.encode可以防止中文乱码
                String fileName = URLEncoder.encode(file.getName(), "UTF-8").replaceAll("\\+", "%20");
                response.setHeader("Content-disposition", "attachment; filename*=utf-8''" + fileName);
//                输出到网页中
                byte[] bytes = new byte[1024];
                int len = 0;
                while ((len = bufferedInputStream.read(bytes)) > 0) {
                    bufferedOutputStream.write(bytes, 0, len);
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return;
        }
    }
}
