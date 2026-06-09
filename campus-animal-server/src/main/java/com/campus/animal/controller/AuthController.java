package com.campus.animal.controller;

import com.campus.animal.common.Result;
import com.campus.animal.dto.LoginDTO;
import com.campus.animal.dto.RegisterDTO;
import com.campus.animal.service.AuthService;
import com.campus.animal.vo.LoginVO;
import com.campus.animal.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器 — 处理用户注册、登录、获取当前用户信息。
 * <p>
 * 路径前缀：{@code /api/auth}
 * <p>
 * 安全策略：
 * <ul>
 *   <li>/register 和 /login 为<b>公开接口</b>（在 SecurityConfig 白名单中放行）</li>
 *   <li>/me 虽不在白名单中，但需要认证（通过 JWT 过滤器获取当前用户）</li>
 * </ul>
 * <p>
 * Controller 层职责（严格遵守分层规范）：
 * <ol>
 *   <li>接收请求参数（通过 @Valid 触发校验）</li>
 *   <li>调用 Service 层方法</li>
 *   <li>将返回值包装为 Result<T> 统一响应格式</li>
 * </ol>
 * 不在 Controller 中写任何业务逻辑。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册。
     * <p>
     * POST /api/auth/register（公开）
     * <p>
     * 请求体示例：
     * <pre>{@code
     * {
     *   "username": "zhangsan",
     *   "password": "123456",
     *   "realName": "张三",
     *   "phone": "13800138000"
     * }
     * }</pre>
     *
     * @param registerDTO 注册信息（@Valid 触发 Jakarta Bean Validation）
     * @return 成功无数据（{@code Result<Void>}）
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        authService.register(registerDTO);
        return Result.success();
    }

    /**
     * 用户登录。
     * <p>
     * POST /api/auth/login（公开）
     * <p>
     * 请求体示例：
     * <pre>{@code
     * { "username": "admin", "password": "admin123" }
     * }</pre>
     * 登录成功返回 JWT 令牌和用户基本信息。
     *
     * @param loginDTO 登录信息
     * @return LoginVO（含 JWT token + 用户信息）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    /**
     * 获取当前登录用户信息。
     * <p>
     * GET /api/auth/me（需认证）
     * <p>
     * 前端可在页面加载时调用此接口，验证 token 是否有效并获取用户信息。
     *
     * @return 当前用户信息（不含密码）
     */
    @GetMapping("/me")
    public Result<UserVO> getCurrentUser() {
        return Result.success(authService.getCurrentUserInfo());
    }

    /**
     * 修改当前用户密码。
     * <p>
     * PUT /api/auth/password（需认证）
     * <p>
     * 请求体：{ "oldPassword": "xxx", "newPassword": "yyy" }
     *
     * @param body 包含 oldPassword 和 newPassword
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body) {
        authService.changePassword(body.get("oldPassword"), body.get("newPassword"));
        return Result.success();
    }

    /**
     * 注销当前用户账号。
     * <p>
     * DELETE /api/auth/account（需认证）
     * <p>
     * 注销后账号不可恢复，由 Service 层从 SecurityContext 获取当前用户。
     */
    @DeleteMapping("/account")
    public Result<Void> deleteAccount() {
        authService.deleteAccount();
        return Result.success();
    }
}
