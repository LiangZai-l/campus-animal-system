package com.campus.animal.service;

import com.campus.animal.dto.LoginDTO;
import com.campus.animal.dto.RegisterDTO;
import com.campus.animal.vo.LoginVO;
import com.campus.animal.vo.UserVO;

/**
 * 用户认证服务接口（接口 + 实现分离，遵循分层架构规范）。
 * <p>
 * 职责：
 * <ul>
 *   <li><b>注册</b>：校验用户名唯一性 → BCrypt 加密密码 → 保存用户</li>
 *   <li><b>登录</b>：校验用户存在/状态正常/密码匹配 → 生成 JWT → 返回 LoginVO</li>
 *   <li><b>获取当前用户</b>：从 SecurityContext 中取出当前登录用户信息</li>
 * </ul>
 */
public interface AuthService {

    /**
     * 用户注册。
     * @param registerDTO 注册请求（用户名、密码、真实姓名、手机号）
     */
    void register(RegisterDTO registerDTO);

    /**
     * 用户登录。
     * @param loginDTO 登录请求（用户名、密码）
     * @return 登录成功响应（JWT token + 用户基本信息）
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 获取当前登录用户的信息。
     * @return 当前用户信息（不含密码）
     */
    UserVO getCurrentUserInfo();

    /**
     * 修改当前用户的密码。
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void changePassword(String oldPassword, String newPassword);

    /**
     * 注销当前用户账号。
     */
    void deleteAccount();
}
