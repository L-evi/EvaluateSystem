package com.project.evaluate.controller.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * @author Levi
 * @version 1.0 (created by Spring Boot)
 * @description
 * @since 2022/12/16 15:23
 */
@Controller
@CrossOrigin(value = "*")
@PropertySource("classpath:application.yml")
public class DownloadController {
    @Value("${file.character-set")
    private static String charaset;

}
