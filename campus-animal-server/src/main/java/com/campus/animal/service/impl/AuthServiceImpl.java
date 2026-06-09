package com.campus.animal.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.animal.common.BusinessException;
import com.campus.animal.common.ResultCode;
import com.campus.animal.dto.LoginDTO;
import com.campus.animal.dto.RegisterDTO;
import com.campus.animal.entity.CheckIn;
import com.campus.animal.entity.User;
import com.campus.animal.mapper.CheckInMapper;
import com.campus.animal.mapper.UserMapper;
import com.campus.animal.security.JwtTokenProvider;
import com.campus.animal.security.SecurityUtils;
import com.campus.animal.security.TokenUser;
import com.campus.animal.service.AuthService;
import com.campus.animal.vo.LoginVO;
import com.campus.animal.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现类。
 * <p>
 * 核心业务流程：
 * <ol>
 *   <li><b>注册</b>：校验用户名唯一 → BCrypt 加密 → 保存（角色固定为 USER）</li>
 *   <li><b>登录</b>：查用户 → 验状态 → 验密码 → 生成 JWT → 封装 LoginVO</li>
 *   <li><b>获取当前用户</b>：从 SecurityContext 取 TokenUser → 查库 → 转 UserVO</li>
 * </ol>
 * <p>
 * 安全设计：
 * <ul>
 *   <li>登录失败不区分"用户不存在"和"密码错误"，统一返回"用户名或密码错误"，
 *       防止攻击者通过错误信息枚举有效用户名</li>
 *   <li>密码使用 BCrypt 加密，每次登录通过 passwordEncoder.matches() 比对</li>
 *   <li>UserVO 不含 password 字段，密码永不出现在响应中</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final CheckInMapper checkInMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 用户注册。
     * <p>
     * 步骤：
     * 1. 用 LambdaQueryWrapper 查询用户名是否已存在
     * 2. 如已存在抛 CONFLICT 异常
     * 3. 创建 User 对象，密码 BCrypt 加密，角色固定为 USER，状态设为正常
     * 4. 插入数据库
     * <p>
     * 为什么注册的角色固定为 USER：管理员由 DataInitializer 在启动时预置，
     * 普通注册只能获得 USER 权限，防止任何人注册 ADMIN 账号。
     */
    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名唯一性
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException(ResultCode.CONFLICT, "用户名已存在");
        }

        // 构建用户对象
        User user = new User();
        user.setUsername(dto.getUsername());
        // BCrypt 加密：相同的明文每次生成的密文都不同（内置随机盐值）
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRealName(dto.getRealName());
        user.setPhone(dto.getPhone());
        user.setRole("USER");       // 固定为普通用户
        user.setStatus(1);          // 账号正常

        userMapper.insert(user);
    }

    /**
     * 用户登录。
     * <p>
     * 三步验证（顺序重要）：
     * <ol>
     *   <li>用户是否存在</li>
     *   <li>账号状态是否正常（status == 1）</li>
     *   <li>密码是否匹配</li>
     * </ol>
     * 任何一步失败都抛出异常，阻止后续步骤执行。
     * <p>
     * 登录成功后生成 JWT 令牌，令牌中携带 userId、username、role，
     * 前端存入 localStorage，后续请求通过 Authorization 头传递。
     */
    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 按用户名查询
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (user == null) {
            // 不区分用户不存在和密码错误，防止用户名枚举
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }

        // 2. 检查账号是否被禁用
        if (user.getStatus() != 1) {
            throw new BusinessException(ResultCode.FORBIDDEN, "账号已被禁用");
        }

        // 3. 验证密码（BCrypt.matches：取出密文中的盐值，对明文加盐后比对）
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "用户名或密码错误");
        }

        // 4. 生成 JWT 令牌
        String token = jwtTokenProvider.generateToken(user.getId(), user.getUsername(), user.getRole());

        // 5. 构建返回对象（使用 Builder 模式）
        return LoginVO.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole())
                .build();
    }

    /**
     * 获取当前登录用户信息。
     * <p>
     * 从 SecurityContextHolder 中取出 JwtAuthenticationFilter 存入的 TokenUser，
     * 然后按 ID 查库获取完整用户信息。这样做的好处是：
     * <ul>
     *   <li>不需要在 JWT 中存储所有用户字段（手机号、头像等可能会更新）</li>
     *   <li>用户修改信息后立即生效，不需要重新登录</li>
     * </ul>
     */
    @Override
    public UserVO getCurrentUserInfo() {
        // 从安全上下文获取当前用户（由 JwtAuthenticationFilter 设置）
        TokenUser tokenUser = SecurityUtils.getCurrentUser();
        if (tokenUser == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录");
        }

        // 查询数据库获取完整用户信息
        User user = userMapper.selectById(tokenUser.getUserId());
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 转换为 VO（排除 password 字段）
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setPhone(user.getPhone());
        vo.setAvatarUrl(user.getAvatarUrl());
        vo.setStatus(user.getStatus());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;
    }

    /**
     * 修改当前用户的密码。
     * <p>
     * 步骤：
     * 1. 验证原密码是否正确
     * 2. BCrypt 加密新密码
     * 3. 更新到数据库
     */
    @Override
    public void changePassword(String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank()
                || newPassword == null || newPassword.isBlank()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "密码不能为空");
        }
        if (newPassword.length() < 6) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "新密码长度不能少于6位");
        }

        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "原密码不正确");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
    }

    /**
     * 注销当前用户账号。
     * <p>
     * 直接删除用户记录，关联的打卡记录会被级联删除（或置空）。
     */
    @Override
    public void deleteAccount() {
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        // 先删除用户的打卡记录，再删除用户
        checkInMapper.delete(new LambdaQueryWrapper<CheckIn>().eq(CheckIn::getUserId, userId));
        userMapper.deleteById(userId);
    }
}
