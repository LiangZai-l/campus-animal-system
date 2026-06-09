package com.campus.animal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求参数 DTO。
 * <p>
 * 用户名和密码除了非空校验外，还加了长度限制：
 * <ul>
 *   <li>用户名：3-50 字符（数据库 unique 约束保证不重复）</li>
 *   <li>密码：6-100 字符（存储的是 BCrypt 密文，长度不受此限制）</li>
 * </ul>
 * 真实姓名和手机号为选填项。
 */
@Data
public class RegisterDTO {

    /** 用户名（必填，3-50 字符） */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度需在3-50之间")
    private String username;

    /** 密码（必填，6-100 字符） */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度需在6-100之间")
    private String password;

    /** 真实姓名（选填） */
    private String realName;

    /** 手机号（选填） */
    private String phone;
}
