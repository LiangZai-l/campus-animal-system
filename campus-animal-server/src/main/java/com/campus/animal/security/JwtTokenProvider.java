package com.campus.animal.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT（JSON Web Token）令牌提供者，负责令牌的生成与解析。
 * <p>
 * JWT 结构（三段式，用 . 分隔）：
 * <ol>
 *   <li><b>Header</b>：签名算法（HS256）和令牌类型（JWT）</li>
 *   <li><b>Payload</b>：业务数据（Claims）— subject=用户名, userId, role, iat, exp</li>
 *   <li><b>Signature</b>：用密钥对前两段进行 HMAC-SHA256 签名，防止篡改</li>
 * </ol>
 * <p>
 * 为什么用 JWT 而不是 Session：
 * <ul>
 *   <li>无状态：服务端不需要存储会话信息，方便水平扩展</li>
 *   <li>自包含：令牌中携带用户信息，减少数据库查询</li>
 *   <li>跨域友好：前端存 localStorage，每次请求带 Authorization 头</li>
 * </ul>
 * <p>
 * 密钥和过期时间从 application.yml 中读取（{@code jwt.secret}、{@code jwt.expiration}）。
 */
@Component
public class JwtTokenProvider {

    /** HMAC-SHA256 签名密钥，由配置中的 Base64 密钥解码得到 */
    private final SecretKey key;

    /** 令牌过期时间（毫秒），默认 24 小时（86400000） */
    private final long expiration;

    /**
     * 构造器注入 JWT 配置。
     * @param secret     Base64 编码的密钥
     * @param expiration 令牌有效期（毫秒）
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        // 将 Base64 密钥解码后创建 HMAC-SHA256 签名密钥
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expiration = expiration;
    }

    /**
     * 生成 JWT 令牌。
     * <p>
     * Claims（荷载）包含：
     * <ul>
     *   <li>sub（subject）— 用户名</li>
     *   <li>userId — 用户 ID</li>
     *   <li>role — 用户角色</li>
     *   <li>iat（issuedAt）— 签发时间</li>
     *   <li>exp（expiration）— 过期时间</li>
     * </ul>
     *
     * @param userId   用户 ID
     * @param username 用户名
     * @param role     角色（ADMIN/USER）
     * @return 签发的 JWT 字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(username)               // 主题 = 用户名
                .claim("userId", userId)         // 自定义声明：用户 ID
                .claim("role", role)             // 自定义声明：角色
                .issuedAt(now)                   // 签发时间
                .expiration(expiry)              // 过期时间
                .signWith(key)                   // 使用密钥签名
                .compact();                      // 生成三段式 JWT 字符串
    }

    /**
     * 解析并验证 JWT 令牌，提取用户信息。
     * <p>
     * 如果令牌过期、签名不匹配、或被篡改，jjwt 库会自动抛出异常，
     * 由调用方（JwtAuthenticationFilter）捕获处理。
     *
     * @param token JWT 字符串
     * @return 解析出的 TokenUser
     * @throws io.jsonwebtoken.JwtException 令牌无效时抛出
     */
    public TokenUser parseToken(String token) {
        // verifyWith(key) 会校验签名；如果已过期也会自动校验
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 从 Payload 中提取用户信息构建 TokenUser
        return new TokenUser(
                claims.get("userId", Long.class),
                claims.getSubject(),
                claims.get("role", String.class)
        );
    }
}
