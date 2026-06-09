package com.campus.animal.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户信息响应 VO。
 * <p>
 * 注意：相比 User 实体，这里没有 {@code password} 字段。
 * 这是安全设计的体现——密码永远不应该返回给前端，
 * 即使密码是 BCrypt 加密的密文也不能泄露。
 */
@Data
public class UserVO {

    /** 用户 ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 角色 */
    private String role;

    /** 手机号 */
    private String phone;

    /** 头像 URL */
    private String avatarUrl;

    /** 账号状态：1=正常，0=禁用 */
    private Integer status;

    /** 注册时间 */
    private LocalDateTime createdAt;
}
