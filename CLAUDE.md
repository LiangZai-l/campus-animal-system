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
│       │   ├── entity/                          # 实体层：User, Animal, CheckIn
│       │   └── mapper/                          # 数据访问层：User/Animal/CheckInMapper
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
| 0. 环境就绪 | ✅ | 数据库 `campus_animal` 已创建，三张表已建，管理员数据已插入 |
| 1. 项目骨架 | ✅ | pom.xml + 启动类 + application.yml 就绪，启动无报错 |
| 2. 实体层与数据访问层 | ✅ | User/Animal/CheckIn 实体 + 对应 Mapper，MapperTest 通过 |
| 3. 统一响应与全局异常处理 | ⏳ 下一步 | Result<T> + BusinessException + GlobalExceptionHandler |
| 4. 用户认证模块 | ⬜ | Spring Security + JWT |
| 5. 动物 CRUD + 文件上传 | ⬜ | |
| 6. 打卡动态模块 | ⬜ | |
| 7. Vue 前端搭建 | ⬜ | |

### 重要备忘

- MyBatis-Plus 3.5.9 移除了 `PaginationInnerInterceptor`，分页由 `MybatisPlusInnerInterceptorAutoConfiguration` 自动配置。无需手动创建 `MybatisPlusConfig`。
- application.yml 中已移除逻辑删除配置（表中无 `deleted` 字段），避免 SQL 报错。
