package org.example.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.RestBean;
import org.example.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 *
 * @author hwshou
 * @date 2025/6/7 18:33
 */
@RestController
@RequestMapping("/api/file")
public class FileController {
    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Resource
    private FileUtils fileUtils;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件名
     */
    @PostMapping("/upload")
    public RestBean<String> uploadsFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return RestBean.failure(400, "文件不能为空");
            }

            String fileName = fileUtils.uploadFile(file);
            logger.info("File uploaded successfully: {}", fileName);
            return RestBean.success(fileName);
        } catch (Exception e) {
            logger.error("File upload failed: {}", e.getMessage(), e);
            return RestBean.failure(500, "文件上传失败：" + e.getMessage());
        }
    }

    /**
     * 下载文件
     *
     * @param name     文件名
     * @param response HTTP响应
     */
    @GetMapping("/download")
    public void download(@RequestParam String name, HttpServletResponse response) {
        try {
            if (name == null || name.trim().isEmpty()) {
                response.setStatus(400);
                response.getWriter().write("文件名不能为空");
                return;
            }

            fileUtils.downloadFile(name, response);
            logger.info("File downloaded successfully: {}", name);
        } catch (Exception e) {
            logger.error("File download failed: {}", e.getMessage(), e);
            try {
                response.setStatus(500);
                response.getWriter().write("文件下载失败：" + e.getMessage());
            } catch (Exception ex) {
                logger.error("Error writing error response: {}", ex.getMessage(), ex);
            }
        }
    }
}
