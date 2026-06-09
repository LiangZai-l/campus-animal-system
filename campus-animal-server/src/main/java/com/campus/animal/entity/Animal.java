package com.campus.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 动物档案实体，映射数据库表 {@code animal}（核心业务表）。
 * <p>
 * 该表是系统的核心，存储校园内所有被记录的流浪动物信息。
 * 与 {@link CheckIn} 是一对多关系（一个动物可被多次打卡）。
 * <p>
 * 索引设计（在数据库中）：
 * <ul>
 *   <li>{@code idx_name} — 支持按名称模糊搜索</li>
 *   <li>{@code idx_type} — 支持按类型筛选</li>
 *   <li>{@code idx_area} — 支持按区域查询</li>
 * </ul>
 */
@Data
@TableName("animal")
public class Animal {

    /** 动物 ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 暂定名字（由管理员命名） */
    private String name;

    /** 动物类型：猫/狗/鸟/其他 */
    private String type;

    /** 常驻区域（如：图书馆附近） */
    private String area;

    /** 特征描述（外貌、性格等） */
    private String description;

    /** 封面照片 URL */
    private String coverImage;

    /** 在校状态：1=在校，0=已离校 */
    private Integer status;

    /** 累计投喂次数（可由打卡数统计） */
    private Integer feederCount;

    /** 创建人 ID（关联 sys_user.id） */
    private Long createdBy;

    /** 录入时间 */
    private LocalDateTime createdAt;

    /** 最后更新时间 */
    private LocalDateTime updatedAt;
}
