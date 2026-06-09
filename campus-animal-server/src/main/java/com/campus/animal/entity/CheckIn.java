package com.campus.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 打卡动态实体，映射数据库表 {@code check_in}。
 * <p>
 * 这是关联表，形成"用户 → 打卡 ← 动物"的星型关联：
 * <ul>
 *   <li>{@code animal_id} — 外键，关联 {@code animal.id}，级联删除</li>
 *   <li>{@code user_id} — 外键，关联 {@code sys_user.id}</li>
 * </ul>
 * <p>
 * 删除动物时，其所有打卡记录会级联删除（数据库 FOREIGN KEY ON DELETE CASCADE）。
 */
@Data
@TableName("check_in")
public class CheckIn {

    /** 打卡 ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联的动物 ID（外键 → animal.id） */
    private Long animalId;

    /** 打卡人 ID（外键 → sys_user.id） */
    private Long userId;

    /** 打卡内容（文字描述） */
    private String content;

    /** 打卡图片 URL 列表（多个逗号分隔） */
    private String images;

    /** 心情：happy=开心, neutral=一般, worried=担心 */
    private String mood;

    /** 打卡地点 */
    private String location;

    /** 打卡时间 */
    private LocalDateTime createdAt;
}
