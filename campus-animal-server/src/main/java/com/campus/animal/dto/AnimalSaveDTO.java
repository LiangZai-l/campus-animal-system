package com.campus.animal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnimalSaveDTO {

    @NotBlank(message = "动物名称不能为空")
    private String name;

    @NotBlank(message = "动物类型不能为空")
    private String type;

    @NotBlank(message = "常驻区域不能为空")
    private String area;

    private String description;

    private String coverImage;

    private Integer status;
}
