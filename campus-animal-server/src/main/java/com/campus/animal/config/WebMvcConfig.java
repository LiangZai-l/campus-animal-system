package com.campus.animal.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

/**
 * Spring MVC 额外配置（静态资源映射）。
 * <p>
 * 核心作用：将 URL 路径 {@code /api/files/**} 映射到本地磁盘的上传目录，
 * 使上传的图片可以通过 URL 直接访问。
 * <p>
 * 示例：
 * <ul>
 *   <li>文件存储位置：{@code ./uploads/a1b2c3.jpg}（相对于项目根目录）</li>
 *   <li>访问 URL：{@code http://localhost:8080/api/files/a1b2c3.jpg}</li>
 * </ul>
 * <p>
 * 注意：上传路径被解析为绝对路径，避免因工作目录切换导致找不到文件。
 * <p>
 * 安全考量：生产环境中建议将上传目录放在应用外部，或使用 OSS/CDN。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** 上传目录的绝对路径 */
    private final String uploadPath;

    /**
     * 构造时从配置中读取上传目录并解析为绝对路径。
     * <p>
     * 使用 {@code user.dir}（当前工作目录）作为基准，
     * 确保不管从哪个目录启动应用，都能正确找到上传文件。
     *
     * @param uploadDir 配置项 file.upload-dir 的值（如 ./uploads）
     */
    public WebMvcConfig(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadPath = Path.of(System.getProperty("user.dir"), uploadDir)
                .toAbsolutePath().normalize().toString();
    }

    /**
     * 添加静态资源映射。
     * <p>
     * {@code addResourceHandler} 定义 URL 路径模式，
     * {@code addResourceLocations} 定义对应的磁盘路径（file: 前缀表示本地文件系统）。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api/files/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
