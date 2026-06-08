package com.campus.animal.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AnimalQueryDTO {

    private String name;
    private String type;
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;
    @Min(value = 1, message = "每页条数必须大于0")
    private Integer size = 10;
}
