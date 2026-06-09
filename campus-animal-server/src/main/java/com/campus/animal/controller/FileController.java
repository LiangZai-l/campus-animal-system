package com.campus.animal.controller;

import com.campus.animal.common.Result;
import com.campus.animal.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器。
 * <p>
 * 路径前缀：{@code /api/files}
 * <p>
 * 上传的文件保存到本地磁盘（配置项 file.upload-dir 指定），
 * 通过 WebMvcConfig 的静态资源映射对外提供访问。
 * <p>
 * 安全考量：
 * <ul>
 *   <li>FileService 校验文件类型为图片（Content-Type）</li>
 *   <li>文件名使用 UUID，防止路径遍历和文件名冲突</li>
 *   <li>文件大小限制在 application.yml 中配置（max-file-size: 10MB）</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传图片文件。
     * <p>
     * POST /api/files/upload（需认证）
     * <p>
     * 前端使用 FormData 上传：
     * <pre>{@code
     * const formData = new FormData();
     * formData.append('file', fileInput.files[0]);
     * axios.post('/api/files/upload', formData);
     * }</pre>
     *
     * @param file 前端上传的文件（表单参数名必须为 "file"）
     * @return 上传后的访问 URL（如 /api/files/a1b2c3.jpg）
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(fileService.upload(file));
    }
}
