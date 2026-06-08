package com.campus.animal.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CheckInVO {
    private Long id;
    private Long userId;
    private String username;
    private String content;
    private String images;
    private String mood;
    private String location;
    private LocalDateTime createdAt;
}
