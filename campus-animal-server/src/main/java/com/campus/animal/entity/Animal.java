package com.campus.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("animal")
public class Animal {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String type;
    private String area;
    private String description;
    private String coverImage;
    private Integer status;
    private Integer feederCount;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
