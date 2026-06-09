package com.campus.animal.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.animal.entity.User;
import com.campus.animal.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 应用启动时的数据初始化器。
 * <p>
 * 实现 {@link CommandLineRunner} 接口，在 Spring 容器初始化完成后、
 * 应用正式接受请求之前执行。适合做数据预置、缓存预热等工作。
 * <p>
 * 当前功能：
 * <ul>
 *   <li>检查数据库中是否存在 admin 用户</li>
 *   <li>如果不存在，自动创建管理员账号（admin / admin123）</li>
 *   <li>如果已存在（如重启应用），不做任何操作，防止覆盖已有密码</li>
 * </ul>
 * <p>
 * 为什么在这里初始化而不是在 SQL 脚本中：
 * <ul>
 *   <li>密码需要 BCrypt 加密，SQL 无法直接生成</li>
 *   <li>幂等性：每次重启不会重复插入</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 应用启动后自动执行。
     * <p>
     * 使用 LambdaQueryWrapper 构建查询条件，避免字符串硬编码字段名，
     * 在字段重命名时 IDE 可以自动重构。
     *
     * @param args 命令行参数（未使用）
     */
    @Override
    public void run(String... args) {
        // 查询 admin 用户是否已存在
        User admin = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, "admin"));

        // 仅当 admin 不存在时才创建（保证幂等性）
        if (admin == null) {
            admin = new User();
            admin.setUsername("admin");
            admin.setRealName("系统管理员");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            // BCrypt 加密密码，数据库中不存储明文
            admin.setPassword(passwordEncoder.encode("admin123"));
            userMapper.insert(admin);
            log.info("管理员账号已初始化: admin / admin123");
        }
    }
}
