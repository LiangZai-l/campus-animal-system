package com.campus.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("check_in")
public class CheckIn {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long animalId;
    private Long userId;
    private String content;
    private String images;
    private String mood;
    private String location;
    private LocalDateTime createdAt;
}
