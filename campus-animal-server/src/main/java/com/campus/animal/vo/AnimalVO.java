package com.campus.animal.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnimalVO {
    private Long id;
    private String name;
    private String type;
    private String area;
    private String description;
    private String coverImage;
    private Integer status;
    private Integer feederCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
