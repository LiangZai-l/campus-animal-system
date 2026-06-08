package com.campus.animal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInSaveDTO {

    @NotNull(message = "动物ID不能为空")
    private Long animalId;

    @NotBlank(message = "打卡内容不能为空")
    private String content;

    private String images;

    private String mood;

    private String location;
}
