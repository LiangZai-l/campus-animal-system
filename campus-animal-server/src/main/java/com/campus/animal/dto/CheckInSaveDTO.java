package com.campus.animal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 发布打卡请求参数 DTO。
 * <p>
 * 必填字段：
 * <ul>
 *   <li>{@code animalId}：关联的动物 ID（前端通过下拉选择动物）</li>
 *   <li>{@code content}：打卡文字内容</li>
 * </ul>
 * 选填字段：
 * <ul>
 *   <li>{@code mood}：心情（happy/neutral/worried）</li>
 *   <li>{@code location}：打卡地点</li>
 *   <li>{@code images}：图片 URL（多个逗号分隔）</li>
 * </ul>
 * <p>
 * 打卡人 ID 不从 DTO 传入，而是在 Service 层通过 SecurityUtils 获取当前登录用户，
 * 防止前端伪造用户身份。
 */
@Data
public class CheckInSaveDTO {

    /** 关联的动物 ID（必填） */
    @NotNull(message = "动物ID不能为空")
    private Long animalId;

    /** 打卡内容（必填） */
    @NotBlank(message = "打卡内容不能为空")
    private String content;

    /** 打卡图片 URL（选填，多个逗号分隔） */
    private String images;

    /** 心情（选填）：happy/neutral/worried */
    private String mood;

    /** 打卡地点（选填） */
    private String location;
}
