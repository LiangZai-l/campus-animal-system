package com.campus.animal.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器，在 Spring Security 过滤器链中执行，每个请求只执行一次。
 * <p>
 * 工作流程：
 * <ol>
 *   <li>从请求头 {@code Authorization: Bearer <token>} 中提取 JWT</li>
 *   <li>如果 token 存在，调用 {@link JwtTokenProvider#parseToken} 解析</li>
 *   <li>解析成功 → 创建 {@link UsernamePasswordAuthenticationToken} 存入 SecurityContext</li>
 *   <li>解析失败（过期/非法） → 不设置认证，让后续过滤器返回 401</li>
 *   <li>无 token → 直接放行（公开接口在 SecurityConfig 中放行，受保护接口会返回 401）</li>
 * </ol>
 * <p>
 * 为什么继承 OncePerRequestFilter 而不是 GenericFilterBean：
 * OncePerRequestFilter 保证同一请求内只执行一次（处理 forward/include 等场景）。
 * <p>
 * 注意：此过滤器不拦截请求，即使 token 无效也放行。
 * 真正的访问控制由 Spring Security 的权限校验（{@code @PreAuthorize}）和
 * SecurityConfig 中的 {@code .anyRequest().authenticated()} 完成。
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. 从请求头中提取 JWT
        String token = extractToken(request);

        if (StringUtils.hasText(token)) {
            TokenUser tokenUser;
            try {
                // 2. 解析令牌（签名校验 + 过期校验）
                tokenUser = jwtTokenProvider.parseToken(token);
            } catch (Exception e) {
                // 令牌无效 → 不设置认证，直接放行
                // 后续 SecurityConfig 的 .authenticated() 会拦截并返回 401
                filterChain.doFilter(request, response);
                return;
            }

            // 3. 构造 Spring Security 的权限对象（ROLE_ 前缀是 Spring Security 规范）
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + tokenUser.getRole())
            );

            // 4. 创建认证令牌 — principal 存 TokenUser，credentials 为空（JWT 无密码）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(tokenUser, null, authorities);

            // 5. 存入安全上下文，后续可通过 SecurityUtils 获取
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 6. 继续执行过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从 HTTP 请求头中提取 Bearer Token。
     * <pre>Authorization: Bearer eyJhbGciOi...</pre>
     * "Bearer " 长度为 7，截取之后的字符串即为 JWT。
     *
     * @param request HTTP 请求
     * @return JWT 字符串，不存在时返回 null
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
