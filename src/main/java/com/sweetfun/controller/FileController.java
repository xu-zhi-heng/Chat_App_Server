package com.sweetfun.controller;

import com.sweetfun.response.Result;
import com.sweetfun.utils.MinioUtil;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {
    private final MinioUtil minioUtil;
    public FileController(MinioUtil minioUtil) {
        this.minioUtil = minioUtil;
    }

    @PostMapping("/uploadFile")
    public Result<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            // 生成唯一文件名，避免覆盖
            String originalName = file.getOriginalFilename();
            String suffix = "";
            if (originalName != null && originalName.contains(".")) {
                suffix = originalName.substring(originalName.lastIndexOf("."));
            }
            String objectName = "files/" + System.currentTimeMillis() + "-" + UUID.randomUUID() + suffix;
            String fileUrl = minioUtil.upload(objectName, file.getInputStream(), file.getContentType());
            return Result.success(fileUrl, "文件上传成功");
        } catch (Exception exception) {
            return Result.error("上传失败：" + exception.getMessage());
        }
    }


    @GetMapping("/getUrl")
    public Result<?> getUrl(@RequestParam("filePath") String filePath) {
        if (filePath == null) {
            return Result.error("文件路径不能为空");
        }
        try {
            String presignedUrl = minioUtil.getPresignedUrl(filePath, 3600);// 有效期 1 小时
            return Result.success(presignedUrl);
        } catch (Exception exception) {
            return Result.error(exception.getMessage());
        }
    }
}
