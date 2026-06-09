package com.campus.animal.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统用户实体，映射数据库表 {@code sys_user}。
 * <p>
 * 使用 MyBatis-Plus 注解：
 * <ul>
 *   <li>{@code @TableName("sys_user")} — 指定映射的数据库表名</li>
 *   <li>{@code @TableId(type = IdType.AUTO)} — 主键自增策略（依赖数据库 AUTO_INCREMENT）</li>
 * </ul>
 * <p>
 * 注意：密码字段 {@code password} 使用 BCrypt 加密存储，数据库中不保存明文。
 * 返回给前端时需在 VO 层排除该字段，防止泄露（Controller 返回 UserVO 而非 User 实体）。
 */
@Data
@TableName("sys_user")
public class User {

    /** 用户 ID，自增主键 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名（登录账号），唯一 */
    private String username;

    /** 密码，BCrypt 加密存储 */
    private String password;

    /** 真实姓名（可选） */
    private String realName;

    /** 角色：ADMIN（管理员）或 USER（普通用户） */
    private String role;

    /** 手机号（可选） */
    private String phone;

    /** 头像 URL（可选） */
    private String avatarUrl;

    /** 账号状态：1=正常，0=禁用 */
    private Integer status;

    /** 创建时间，数据库自动填充 */
    private LocalDateTime createdAt;

    /** 最后修改时间，数据库自动更新 */
    private LocalDateTime updatedAt;
}
