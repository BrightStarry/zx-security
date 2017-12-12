package com.zx.web.controller;

import com.zx.dto.FileInfo;
import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017-11-19 15:56
 * 文件
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private String folder = "H:\\ideaworkspace\\zx-security\\zx-security-demo\\src\\main\\java\\com\\zx\\web";

    //文件上传
    @PostMapping
    public FileInfo upload(MultipartFile file) throws Exception {
        System.out.println(file.getName());
        System.out.println(file.getOriginalFilename());
        System.out.println(file.getSize());

        //存放的文件夹

        File localFile = new File(folder, new Date().getTime() + ".txt");
        //将传入的文件写入本地,其实现原理还是FileCopyUtils
        //如果不写入本地,可以使用file.getInputStream()获取流
        file.transferTo(localFile);

        //返回上传的文件信息
        return new FileInfo(localFile.getAbsolutePath());
    }

    //文件下载
    @GetMapping("/{id}")
    public void download(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) throws IOException {
//            try (InputStream inputStream = new FileInputStream(new File(folder, id + ".txt"));
//                 OutputStream outputStream = response.getOutputStream()) {
//                //定义类型和下载过去的文件名
//                response.setContentType("application/x-download");
//                response.addHeader("Content-Disposition","attachment;filename=test.txt");
//
//                //将输入流输出到输出流
//                IOUtils.copy(inputStream, outputStream);
//                outputStream.flush();
//            }

        @Cleanup InputStream inputStream = new FileInputStream(new File(folder, id + ".txt"));
        @Cleanup OutputStream outputStream = response.getOutputStream();
        //定义类型和下载过去的文件名
        response.setContentType("application/x-download");
        response.addHeader("Content-Disposition", "attachment;filename=test.txt");

        //将输入流输出到输出流
        IOUtils.copy(inputStream, outputStream);
        outputStream.flush();
    }

}
