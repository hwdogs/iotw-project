package org.example.utils;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件上传工具类
 *
 * @author hwshou
 * @date 2025/6/7 18:37
 */
@Component
public class FileUtils {

    @Value("${images.path}")
    private String basePath;

    /**
     * 上传图片
     *
     * @param file 上传的文件
     * @return 文件名
     */
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }

        // 获取文件扩展名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 使用UUID生成文件名，防止文件名重复
        String fileName = UUID.randomUUID().toString() + suffix;

        // 确保目录存在
        File dir = new File(basePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("创建目录失败: " + basePath);
        }

        try {
            // 将文件转存到指定位置
            File destFile = new File(basePath + fileName);
            file.transferTo(destFile);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 文件下载
     *
     * @param fileName 文件名
     * @param response HTTP响应
     */
    public void downloadFile(String fileName, HttpServletResponse response) {
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new RuntimeException("文件名不能为空");
        }

        File file = new File(basePath + fileName);
        if (!file.exists()) {
            throw new RuntimeException("文件不存在: " + fileName);
        }

        try (FileInputStream fileInputStream = new FileInputStream(file);
                ServletOutputStream outputStream = response.getOutputStream()) {

            response.setContentType("image/jpeg");

            byte[] buffer = new byte[1024];
            int len;
            while ((len = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }
}
