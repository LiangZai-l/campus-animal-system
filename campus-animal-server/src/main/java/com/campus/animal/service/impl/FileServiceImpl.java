package com.campus.animal.service.impl;

import com.campus.animal.common.BusinessException;
import com.campus.animal.common.ResultCode;
import com.campus.animal.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/**
 * 文件上传服务实现类。
 * <p>
 * 上传流程：
 * <ol>
 *   <li>校验文件非空</li>
 *   <li>校验 Content-Type 必须是 image/* 类型</li>
 *   <li>根据 Content-Type 确定文件扩展名（使用 JDK 21 的 switch 表达式）</li>
 *   <li>生成 UUID 文件名（防冲突 + 防路径遍历攻击）</li>
 *   <li>保存到本地磁盘</li>
 *   <li>返回可访问的 URL：{@code /api/files/{uuid}.{ext}}</li>
 * </ol>
 * <p>
 * 安全设计：
 * <ul>
 *   <li>扩展名从 Content-Type 推导，不信任用户上传的文件名（防 XSS/路径遍历）</li>
 *   <li>UUID 文件名无法预测，防止恶意遍历</li>
 *   <li>仅允许图片类型，防止上传 JSP/HTML/EXE 等恶意文件</li>
 * </ul>
 * <p>
 * 生产环境建议：使用对象存储（OSS/S3/CDN）代替本地磁盘，
 * 解决多实例部署时文件不共享和磁盘空间有限的问题。
 */
@Slf4j
@Service
public class FileServiceImpl implements FileService {

    /** 上传目录的绝对路径，构造时从配置读取并创建目录 */
    private final Path uploadPath;

    /**
     * 构造器注入上传目录配置，并确保目录存在。
     * <p>
     * 使用 {@code user.dir} 作为基准路径，确保相对路径能正确解析为绝对路径。
     * Files.createDirectories 在目录已存在时不会报错，所以重启安全。
     *
     * @param uploadDir 配置项 file.upload-dir 的值（如 ./uploads）
     */
    public FileServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadPath = Path.of(System.getProperty("user.dir"), uploadDir).toAbsolutePath().normalize();
        try {
            // 确保上传目录存在（目录已存在时不报错）
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + uploadPath, e);
        }
    }

    /**
     * 上传图片文件。
     *
     * @param file 前端上传的文件
     * @return 上传后的访问 URL
     */
    @Override
    public String upload(MultipartFile file) {
        // 1. 校验文件非空
        if (file.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "文件不能为空");
        }

        // 2. 校验 Content-Type 是否为图片类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只允许上传图片文件");
        }

        // 3. 根据 Content-Type 确定文件扩展名（JDK 21 switch 表达式）
        String extension = switch (contentType) {
            case "image/jpeg" -> ".jpg";
            case "image/png"  -> ".png";
            case "image/gif"  -> ".gif";
            case "image/webp" -> ".webp";
            default           -> ".jpg";  // 未知图片类型默认用 .jpg
        };

        // 4. 生成 UUID 文件名（防冲突、防路径遍历、防预测）
        String filename = UUID.randomUUID().toString() + extension;

        // 5. 保存文件到磁盘
        try {
            file.transferTo(uploadPath.resolve(filename).toFile());
        } catch (IOException e) {
            log.error("文件上传失败: ", e);
            throw new BusinessException("文件上传失败: " + e.getMessage());
        }

        // 6. 返回可访问 URL（通过 WebMvcConfig 的静态资源映射对外提供服务）
        return "/api/files/" + filename;
    }
}
