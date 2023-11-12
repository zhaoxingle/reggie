package com.zxl.controller;

import com.zxl.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) {
        log.info("upload!");
        String originalFilename = file.getOriginalFilename();
        //使用uuid重新生成文件名，防止 文件名重复覆
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = UUID.randomUUID() + suffix;

        File file1 = new File(basePath);
        if (!file1.exists()) {
            boolean mkdirs = file1.mkdirs();
            log.info("mkdir" + file1.toString());
        }
        //保存文件
        try {
            file.transferTo(new File(basePath + fileName));
            //可以通过调用 transferTo 方法将文件内容保存到指定的目标文件中。
            //void transferTo(File dest) throws IOException;
            //其中，dest 参数是保存上传文件内容的目标文件。
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(fileName);
    }

    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        try {
            //输入流，将文件读取
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            ServletOutputStream outputStream = response.getOutputStream();
            byte[] file = new byte[1024];
            int len = 0;
            response.setContentType("image/jpeg");
            while ((len = fileInputStream.read(file)) != -1) {
                outputStream.write(file, 0, len);
                outputStream.flush();
            }

            fileInputStream.close();
            outputStream.close();
            //输出流，将文件读到response中
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
