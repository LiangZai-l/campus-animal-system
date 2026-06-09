package com.campus.animal.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 登录成功响应 VO。
 * <p>
 * VO（View Object）是服务端返回给前端的展示对象，
 * 与 Entity 的区别：
 * <ul>
 *   <li>VO 不包含敏感字段（如密码）</li>
 *   <li>VO 可以跨表聚合多个实体的数据</li>
 *   <li>VO 字段命名和结构更贴合前端展示需求</li>
 * </ul>
 * <p>
 * 使用 {@code @Builder} 注解支持建造者模式构建对象。
 */
@Data
@Builder
public class LoginVO {

    /** JWT 令牌，前端存入 localStorage，后续请求携带在 Authorization 头中 */
    private String token;

    /** 用户 ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 真实姓名（用于页面展示） */
    private String realName;

    /** 角色：ADMIN / USER（前端据此判断是否显示管理菜单） */
    private String role;
}
