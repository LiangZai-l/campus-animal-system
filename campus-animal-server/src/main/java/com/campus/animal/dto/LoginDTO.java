package com.campus.animal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求参数 DTO。
 * <p>
 * DTO（Data Transfer Object）用于接收前端请求数据，
 * 与 Entity 的区别：
 * <ul>
 *   <li>DTO 只包含接口需要的字段，不暴露数据库全部字段</li>
 *   <li>DTO 带有校验注解（{@code @NotBlank}），在 Controller 层通过 {@code @Valid} 触发校验</li>
 *   <li>DTO 不映射数据库表，可跨表组合多个实体的字段</li>
 * </ul>
 */
@Data
public class LoginDTO {

    /** 用户名（必填） */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 密码（必填，明文传输，后端用 BCrypt 比对） */
    @NotBlank(message = "密码不能为空")
    private String password;
}
