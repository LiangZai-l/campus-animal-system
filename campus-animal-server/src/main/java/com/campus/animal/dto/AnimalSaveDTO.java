package com.campus.animal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 动物档案新增/修改请求参数 DTO。
 * <p>
 * 新增和修改共用同一个 DTO，通过 Controller 路径区分：
 * <ul>
 *   <li>POST /api/animals → 新增（AnimalController.create）</li>
 *   <li>PUT /api/animals/{id} → 修改（AnimalController.update）</li>
 * </ul>
 * <p>
 * 必填字段：name（名称）、type（类型）、area（区域）。
 * description、coverImage、status 为选填。
 */
@Data
public class AnimalSaveDTO {

    /** 动物名称（必填） */
    @NotBlank(message = "动物名称不能为空")
    private String name;

    /** 动物类型（必填）：猫/狗/鸟/其他 */
    @NotBlank(message = "动物类型不能为空")
    private String type;

    /** 常驻区域（必填）：如"图书馆附近" */
    @NotBlank(message = "常驻区域不能为空")
    private String area;

    /** 特征描述（选填） */
    private String description;

    /** 封面图片 URL（选填） */
    private String coverImage;

    /** 在校状态（选填）：1=在校，0=已离校 */
    private Integer status;
}
