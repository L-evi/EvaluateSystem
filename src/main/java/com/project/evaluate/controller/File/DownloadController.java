package com.project.evaluate.controller.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/16 15:23
 */
@Controller
@CrossOrigin("*")
@PropertySource("classpath:application.yml")
public class DownloadController {
    @Value("${file.character-set")
    private static String charaset;

    @GetMapping("/download/{filePath}")
    public void downloadFile(@PathVariable("filePath") String filePath, HttpServletResponse response) throws IOException {

    }
}
