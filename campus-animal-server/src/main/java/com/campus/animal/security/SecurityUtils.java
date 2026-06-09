package com.campus.animal.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类，用于在业务代码中获取当前登录用户的信息。
 * <p>
 * 核心原理：
 * <ul>
 *   <li>{@link SecurityContextHolder} 是 Spring Security 的全局安全上下文容器</li>
 *   <li>{@link JwtAuthenticationFilter} 在请求进入时解析 JWT 并将用户信息存入其中</li>
 *   <li>同一请求线程内的任何地方都可以通过本工具类取出当前用户</li>
 * </ul>
 * <p>
 * 这是一个纯工具类，构造器私有化，所有方法均为静态方法。
 * 使用 {@code SecurityContextHolder.getContext().getAuthentication()} 获取认证信息，
 * 然后从中提取 {@link TokenUser}。
 */
public class SecurityUtils {

    /** 私有构造器，防止实例化工具类 */
    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 获取当前请求的登录用户完整信息。
     * <p>
     * 实现细节：SecurityContextHolder 使用 ThreadLocal 存储安全上下文，
     * 因此同一请求线程内任何位置调用此方法都能拿到当前用户。
     *
     * @return 当前登录用户的 TokenUser，未登录时返回 null
     */
    public static TokenUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof TokenUser tokenUser) {
            return tokenUser;
        }
        return null;
    }

    /**
     * 获取当前登录用户的 ID。
     * @return 用户 ID，未登录时返回 null
     */
    public static Long getCurrentUserId() {
        TokenUser user = getCurrentUser();
        return user != null ? user.getUserId() : null;
    }

    /**
     * 获取当前登录用户的用户名。
     * @return 用户名，未登录时返回 null
     */
    public static String getCurrentUsername() {
        TokenUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 获取当前登录用户的角色。
     * @return 角色字符串（ADMIN/USER），未登录时返回 null
     */
    public static String getCurrentUserRole() {
        TokenUser user = getCurrentUser();
        return user != null ? user.getRole() : null;
    }
}
