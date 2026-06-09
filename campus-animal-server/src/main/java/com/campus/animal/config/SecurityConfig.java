package com.campus.animal.config;

import com.campus.animal.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 核心配置类。
 * <p>
 * 三大核心注解：
 * <ul>
 *   <li>{@code @EnableWebSecurity}：启用 Spring Security 的 Web 安全支持</li>
 *   <li>{@code @EnableMethodSecurity}：启用方法级权限注解（{@code @PreAuthorize}、{@code @PostAuthorize}）</li>
 *   <li>{@code @Configuration}：声明这是一个 Spring 配置类</li>
 * </ul>
 * <p>
 * 安全策略概述：
 * <ol>
 *   <li><b>无状态（Stateless）</b>：禁用 Session，每次请求通过 JWT 认证</li>
 *   <li><b>禁用 CSRF</b>：前后端分离 + JWT 场景下不需要 CSRF 保护</li>
 *   <li><b>白名单</b>：注册、登录、文件访问、API 文档公开可访问</li>
 *   <li><b>JWT 过滤器</b>：在 Spring Security 的认证过滤器之前插入自定义 JWT 过滤器</li>
 *   <li><b>JSON 异常响应</b>：认证失败和权限不足时返回 JSON 而非默认的 HTML 页面</li>
 * </ol>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码编码器 Bean。
     * <p>
     * BCrypt 是一种自适应哈希算法，内置 salt（盐值），
     * 即使两个用户密码相同，加密后的密文也不同。
     * 加密强度（log_rounds）默认为 10，即 2^10 轮哈希迭代。
     *
     * @return BCryptPasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security 过滤器链配置。
     * <p>
     * 过滤器链执行顺序（关键节点）：
     * <ol>
     *   <li>SecurityContextPersistenceFilter（安全上下文持久化）</li>
     *   <li><b>JwtAuthenticationFilter</b>（我们的 JWT 过滤器，在 UsernamePasswordAuthenticationFilter 之前）</li>
     *   <li>UsernamePasswordAuthenticationFilter（表单登录，我们不用）</li>
     *   <li>FilterSecurityInterceptor（授权判断）</li>
     * </ol>
     * <p>
     * 白名单说明：
     * <ul>
     *   <li>GET /api/files/** — 上传的图片需要公开访问</li>
     *   <li>/api/auth/register、/api/auth/login — 认证接口</li>
     *   <li>/swagger-ui.html、/doc.html 等 — Knife4j API 文档</li>
     *   <li>/error — Spring Boot 默认错误页面</li>
     * </ul>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 禁用 CSRF（前后端分离 + 无状态 JWT 不需要）
            .csrf(csrf -> csrf.disable())

            // 2. 设置为无状态会话（不创建 HttpSession）
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. 异常处理 — 返回 JSON 而非默认的 HTML 页面
            .exceptionHandling(ex -> ex
                // 未认证（没有 token 或 token 无效） → 401
                .authenticationEntryPoint((req, res, e) -> {
                    res.setContentType("application/json;charset=UTF-8");
                    res.setStatus(HttpStatus.UNAUTHORIZED.value());
                    res.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}");
                })
                // 权限不足（角色不匹配） → 403
                .accessDeniedHandler((req, res, e) -> {
                    res.setContentType("application/json;charset=UTF-8");
                    res.setStatus(HttpStatus.FORBIDDEN.value());
                    res.getWriter().write("{\"code\":403,\"message\":\"权限不足\",\"data\":null}");
                })
            )

            // 4. URL 访问控制规则（顺序重要：先匹配的先生效）
            .authorizeHttpRequests(auth -> auth
                // 公开：文件访问（GET 方式）
                .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                // 公开：注册、登录、API 文档
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/swagger-ui.html",
                    "/doc.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/webjars/**"
                ).permitAll()
                // 公开：Spring Boot 错误页面
                .requestMatchers("/error").permitAll()
                // 其余所有请求需要认证
                .anyRequest().authenticated()
            )

            // 5. 在 UsernamePasswordAuthenticationFilter 之前插入 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
