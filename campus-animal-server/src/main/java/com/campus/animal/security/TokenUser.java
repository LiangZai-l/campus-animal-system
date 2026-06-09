package com.campus.animal.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * JWT 令牌中携带的用户主体信息。
 * <p>
 * 与 Spring Security 的 {@code UserDetails} 不同，TokenUser 更轻量，
 * 只存储从 JWT 中解析出的核心字段（用户ID、用户名、角色），
 * 不存储密码，因为 JWT 认证是无状态的——密码验证已在登录时完成。
 * <p>
 * 该类会被放入 {@code SecurityContextHolder}，之后通过 {@link SecurityUtils}
 * 在任何业务代码中获取当前登录用户信息。
 */
@Getter
@AllArgsConstructor
public class TokenUser {

    /** 用户 ID（数据库主键） */
    private final Long userId;

    /** 用户名（登录账号） */
    private final String username;

    /** 角色：ADMIN 或 USER */
    private final String role;
}
