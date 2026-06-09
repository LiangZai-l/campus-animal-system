package com.campus.animal.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务接口。
 * <p>
 * 职责：
 * <ul>
 *   <li>校验文件类型（仅允许图片格式：JPEG、PNG、GIF、WebP）</li>
 *   <li>生成 UUID 文件名（防止用户上传的原始文件名引发冲突或 XSS）</li>
 *   <li>保存到本地磁盘（配置项 file.upload-dir 指定的目录）</li>
 *   <li>返回可访问的 URL（/api/files/{filename}）</li>
 * </ul>
 * <p>
 * 安全设计要点：
 * <ul>
 *   <li>文件扩展名从 Content-Type 推导，而非信任用户提供的文件名</li>
 *   <li>UUID 文件名避免路径遍历攻击</li>
 *   <li>限制文件类型为图片，防止上传恶意脚本</li>
 * </ul>
 */
public interface FileService {

    /**
     * 上传图片文件。
     * @param file 来自前端的 MultipartFile
     * @return 上传后可访问的图片 URL
     */
    String upload(MultipartFile file);
}
