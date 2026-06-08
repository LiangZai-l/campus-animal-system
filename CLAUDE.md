# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概况

基于 Spring Boot 3 的校园流浪动物图鉴与动态打卡系统 — 前后端分离项目，用于学习 Java 后端开发全流程。

详细架构、数据库设计、API 设计见 `项目开发方案.md`。

## 技术栈

- 后端：Spring Boot 3.x + JDK 21 + MyBatis-Plus 3.5+ + MySQL 8.0 + Spring Security + JWT
- 前端：Vue 3 + Vite + Element Plus + Axios
- 构建：Maven 3.9+
- 开发工具：IntelliJ IDEA Community / Trae CN

## 本机环境

| 组件 | 路径 |
|------|------|
| JDK 21 | `D:\JAVA\Develop\jdk` |
| Maven | `D:\JAVA\apache-maven-3.9.14` |
| MySQL | `C:\Program Files\MySQL\MySQL Server 8.0`（服务名 MySQL80） |

## 目录结构

```
campus-animal-server/                    # 后端 Spring Boot
│   pom.xml
│   └── src/
│       ├── main/java/com/campus/animal/
│       │   ├── CampusAnimalApplication.java     # 启动类
│       │   ├── common/                          # 公共模块
│       │   │   ├── Result.java                  # 统一响应体
│       │   │   ├── ResultCode.java              # 状态码常量
│       │   │   ├── BusinessException.java       # 业务异常
│       │   │   └── GlobalExceptionHandler.java  # 全局异常处理
│       │   ├── config/                          # 配置层
│       │   │   ├── SecurityConfig.java          # Spring Security + JWT
│       │   │   ├── MybatisPlusConfig.java       # 分页插件
│       │   │   ├── WebMvcConfig.java            # 静态资源映射
│       │   │   └── DataInitializer.java         # 管理员初始化
│       │   ├── controller/                      # 控制器层
│       │   │   ├── AuthController.java          # 注册/登录
│       │   │   ├── AnimalController.java        # 动物 CRUD
│       │   │   └── FileController.java          # 文件上传
│       │   ├── service/                         # 业务逻辑层
│       │   │   ├── AuthService.java / AnimalService.java / FileService.java
│       │   │   └── impl/                        # 实现类
│       │   ├── mapper/                          # 数据访问层
│       │   │   └── User/Animal/CheckInMapper.java
│       │   ├── entity/                          # 实体层
│       │   │   └── User/Animal/CheckIn.java
│       │   ├── dto/                             # 入参 DTO
│       │   │   └── LoginDTO / RegisterDTO / AnimalSaveDTO / AnimalQueryDTO
│       │   ├── vo/                              # 出参 VO
│       │   │   └── LoginVO / UserVO / AnimalVO / AnimalDetailVO / CheckInVO
│       │   └── security/                        # 安全模块
│       │       ├── JwtTokenProvider.java        # JWT 生成与解析
│       │       ├── JwtAuthenticationFilter.java # JWT 认证过滤器
│       │       ├── TokenUser.java               # 令牌用户主体
│       │       └── SecurityUtils.java           # 获取当前用户工具
│       ├── main/resources/
│       │   └── application.yml                  # 核心配置
│       └── test/java/com/campus/animal/
│           └── MapperTest.java                  # 数据库连通性测试
campus-animal-web/                       # 前端 Vue 3（待创建）
项目开发方案.md                           # 完整方案文档
```

## 常用命令

```bash
# 后端
cd campus-animal-server
mvn spring-boot:run          # 启动后端（端口 8080）
mvn clean package            # 打包
mvn test -Dtest=MapperTest   # 运行数据访问层测试

# 前端
cd campus-animal-web
npm install                  # 安装依赖
npm run dev                  # 启动前端（端口 5173）

# 数据库
# MySQL 服务已设为自动启动，通过 MySQL Workbench 管理
# 数据库名：campus_animal，字符集 utf8mb4
```

## 核心约定

- 分层架构：Controller → Service → Mapper，单向依赖，不可跨层调用
- 统一响应：所有接口返回 `Result<T>`（code + message + data）
- Controller 只做参数接收和路由，业务逻辑全部在 Service 层
- MyBatis-Plus 查询使用 `LambdaQueryWrapper`，避免字符串硬编码字段名
- 密码使用 BCrypt 加密，敏感字段不返回给前端（`@JsonIgnore`）
- 接口路径前缀 `/api`，RESTful 风格（名词复数 + HTTP 方法语义）
- 包路径：`com.campus.animal`

## 项目当前状态

| 阶段 | 状态 | 说明 |
|------|------|------|
| 0. 环境就绪 | ✅ | 数据库 `campus_animal` 已创建，三张表已建 |
| 1. 项目骨架 | ✅ | pom.xml + 启动类 + application.yml 就绪，启动无报错 |
| 2. 实体层与数据访问层 | ✅ | User/Animal/CheckIn 实体 + 对应 Mapper，MapperTest 通过 |
| 3. 统一响应与全局异常处理 | ✅ | Result<T> + BusinessException + GlobalExceptionHandler + ResultCode |
| 4. 用户认证模块 | ✅ | Spring Security + JWT（注册/登录/JWT过滤器/权限控制） |
| 5. 动物 CRUD + 文件上传 | ✅ | 分页模糊查询/类型筛选/文件上传/ADMIN 权限隔离 |
| 6. 打卡动态模块 | ⬜ | |
| 7. Vue 前端搭建 | ⬜ | |

### 重要备忘

- MyBatis-Plus 3.5.9 将 `PaginationInnerInterceptor` 拆分到了独立模块 `mybatis-plus-jsqlparser`（不再是核心模块的一部分）。因此 pom.xml 需同时引入两个依赖，并创建 `MybatisPlusConfig` 手动注册分页插件。
- application.yml 中已移除逻辑删除配置（表中无 `deleted` 字段），避免 SQL 报错。
- 管理员账号由 `DataInitializer` 在首次启动时自动创建（admin / admin123），仅当数据库中无 admin 用户时创建，不会覆盖已有密码。
- `GlobalExceptionHandler` 中的 `AccessDeniedException` 处理用于 `@PreAuthorize` 方法级权限校验（如 ADMIN 操作），`AuthenticationException` 处理由 `SecurityConfig` 中的 `authenticationEntryPoint` 在过滤器层统一处理。
- 文件上传扩展名从 Content-Type 推导（非用户文件名），防止 XSS 攻击。
- `AnimalQueryDTO` 的 `page` 和 `size` 字段已加 `@Min(1)` 校验，防止负值导致 SQL 异常。

## Git 与网络

远程仓库：`https://github.com/LiangZai-l/campus-animal-system.git`

```bash
# 提交并推送
cd campus-animal-server/..
git add -A
git commit -m "..."
git push origin main
```

如果 `git push` 无法访问 GitHub（境外服务器），可配置代理后再推送：

```bash
git config http.proxy http://127.0.0.1:10808
git push origin main
git config --unset http.proxy  # 推送完取消代理
```

> **注意：** 如果遇到网络问题导致 `git push` 失败，请向用户确认后走 10808 端口代理。
