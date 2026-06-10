# 🐾 校园流浪动物图鉴与动态打卡系统

基于 Spring Boot 3 + Vue 3 的前后端分离项目，用于校园流浪动物的图鉴管理与动态打卡。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen)](https://spring.io/projects/spring-boot)
[![JDK](https://img.shields.io/badge/JDK-21-orange)](https://openjdk.org/projects/jdk/21/)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.9-blue)](https://baomidou.com/)
[![Vue](https://img.shields.io/badge/Vue-3.4-4fc08d)](https://vuejs.org/)
[![Element Plus](https://img.shields.io/badge/Element%20Plus-2.7-409eff)](https://element-plus.org/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-4479a1)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/license-MIT-green)](./LICENSE)

---

## 目录

- [功能概览](#功能概览)
- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [数据库设计](#数据库设计)
- [API 文档](#api-文档)
- [快速开始](#快速开始)
- [默认账号](#默认账号)
- [界面截图](#界面截图)
- [安全设计](#安全设计)
- [开发约定](#开发约定)

---

## 功能概览

### 普通用户（USER）

| 模块 | 功能 |
|------|------|
| 首页 | 项目横幅、当日热门动物路径图、动物卡片列表 |
| 动物图鉴 | 分页浏览、名称模糊搜索、按类型筛选 |
| 动物详情 | 档案信息 + 打卡时间轴 + 一键打卡入口 |
| 发布打卡 | 选择动物、填写内容、添加心情与地点 |
| 个人中心 | 打卡历史管理、修改密码、注销账号 |

### 管理员（ADMIN）

| 模块 | 功能 |
|------|------|
| 档案管理 | 动物列表表格、新增/编辑/删除动物 |
| 图片上传 | 动物封面上传（仅限图片格式） |
| 打卡管理 | 在动物详情页直接删除不当打卡记录 |

---

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.3.5 |
| JDK | Java | 21 LTS |
| 持久层 | MyBatis-Plus | 3.5.9 |
| 数据库 | MySQL | 8.0 |
| 认证授权 | Spring Security + JWT (jjwt) | 0.12.6 |
| API 文档 | Knife4j (Swagger 增强) | 4.5.0 |
| 参数校验 | Jakarta Bean Validation | — |
| 前端框架 | Vue 3 + Vite | 3.4 / 5.4 |
| UI 组件库 | Element Plus | 2.7 |
| HTTP 客户端 | Axios | 1.7 |
| 状态管理 | Pinia | 2.1 |
| 路由 | Vue Router | 4.3 |
| 构建工具 | Maven | 3.9+ |

---

## 项目结构

```
campus-animal-system/
├── campus-animal-server/                # 后端 Spring Boot
│   ├── pom.xml
│   └── src/main/java/com/campus/animal/
│       ├── CampusAnimalApplication.java     # 启动类
│       ├── common/                          # 公共模块
│       │   ├── Result.java                  # 统一响应体 {code, message, data}
│       │   ├── ResultCode.java              # HTTP 状态码常量
│       │   ├── BusinessException.java       # 自定义业务异常
│       │   └── GlobalExceptionHandler.java  # @RestControllerAdvice 全局异常处理
│       ├── config/                          # 配置层
│       │   ├── SecurityConfig.java          # Spring Security + JWT 无状态配置
│       │   ├── MybatisPlusConfig.java       # 分页插件注册
│       │   ├── WebMvcConfig.java            # 静态资源映射
│       │   └── DataInitializer.java         # 管理员账号初始化
│       ├── controller/                      # 控制器层 (仅做参数接收和路由)
│       │   ├── AuthController.java          # 注册 / 登录
│       │   ├── AnimalController.java        # 动物 CRUD
│       │   ├── CheckInController.java       # 打卡发布 / 时间轴 / 管理员删除
│       │   └── FileController.java          # 文件上传
│       ├── service/                         # 业务逻辑层
│       │   ├── AuthService / AnimalService / CheckInService / FileService
│       │   └── impl/                        # 实现类
│       ├── mapper/                          # 数据访问层 (MyBatis-Plus BaseMapper)
│       │   └── User / Animal / CheckInMapper
│       ├── entity/                          # 数据库实体 (User / Animal / CheckIn)
│       ├── dto/                             # 入参 DTO (含 @Valid 校验注解)
│       ├── vo/                              # 出参 VO (可跨表聚合)
│       └── security/                        # 安全模块
│           ├── JwtTokenProvider.java        # JWT 生成 / 解析 / 校验
│           ├── JwtAuthenticationFilter.java # OncePerRequestFilter JWT 过滤器
│           ├── TokenUser.java               # 轻量用户主体
│           └── SecurityUtils.java           # SecurityContextHolder 工具类
│
├── campus-animal-web/                  # 前端 Vue 3
│   ├── package.json
│   ├── vite.config.js                     # Vite 配置 (dev proxy → 8080)
│   └── src/
│       ├── main.js                        # 应用入口 (Element Plus + 图标全局注册)
│       ├── style.css                      # 全局主题变量 (鼠尾草绿主题)
│       ├── App.vue                        # 根组件
│       ├── api/index.js                   # Axios 封装 (拦截器 + 错误处理)
│       ├── router/index.js                # 路由表 + 导航守卫
│       ├── stores/auth.js                 # Pinia 认证状态 (localStorage 持久化)
│       ├── assets/logo.svg                # Logo (爪印 + 绿叶)
│       ├── components/
│       │   ├── AppHeader.vue              # 顶部导航栏 (用户下拉菜单)
│       │   ├── AnimalCard.vue             # 动物卡片 (封面图 + emoji 占位)
│       │   └── CheckInTimeline.vue        # 打卡时间轴 (支持管理员删除按钮)
│       └── views/
│           ├── LoginView.vue              # 登录页
│           ├── RegisterView.vue           # 注册页
│           ├── HomeView.vue               # 首页 (横幅 + 热门路径 + 卡片列表)
│           ├── AnimalDetailView.vue       # 动物详情 (档案 + 打卡时间轴)
│           ├── AnimalFormView.vue         # 新增/编辑动物 (复用组件)
│           ├── CheckInCreateView.vue      # 发布打卡
│           ├── ProfileView.vue            # 个人中心
│           └── AdminDashboard.vue         # 管理后台 (表格 + 分页)
│
└── .gitignore
```

---

## 数据库设计

**数据库名：** `campus_animal` &nbsp;|&nbsp; **字符集：** `utf8mb4` &nbsp;|&nbsp; **引擎：** `InnoDB`

### 实体关系图

```
┌──────────────┐       ┌──────────────┐       ┌──────────────┐
│   sys_user   │       │   check_in   │       │    animal    │
│  (系统用户)   │ 1 ── N │  (打卡动态)   │ N ── 1 │  (动物档案)   │
└──────────────┘       └──────────────┘       └──────────────┘
     │                                              │
     │ id ────────────────────────── created_by ────┘
     │
     └── role: ADMIN / USER
```

### 表结构

<details>
<summary><b>sys_user</b> — 系统用户表</summary>

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| username | VARCHAR(50) UNIQUE NOT NULL | 用户名 |
| password | VARCHAR(255) NOT NULL | BCrypt 加密 |
| real_name | VARCHAR(50) | 真实姓名 |
| role | VARCHAR(20) DEFAULT 'USER' | ADMIN / USER |
| phone | VARCHAR(20) | 手机号 |
| avatar_url | VARCHAR(255) | 头像 URL |
| status | TINYINT DEFAULT 1 | 1-正常 0-禁用 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

</details>

<details>
<summary><b>animal</b> — 动物档案表</summary>

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| name | VARCHAR(50) NOT NULL | 暂定名字 |
| type | VARCHAR(30) NOT NULL | 猫 / 狗 / 鸟 / 其他 |
| area | VARCHAR(100) NOT NULL | 常驻区域 |
| description | TEXT | 特征描述 |
| cover_image | VARCHAR(255) | 封面照片 URL |
| status | TINYINT DEFAULT 1 | 1-在校 0-已离校 |
| feeder_count | INT DEFAULT 0 | 累计投喂次数 |
| created_by | BIGINT | 创建人 ID |
| created_at | DATETIME | 录入时间 |
| updated_at | DATETIME | 更新时间 |

</details>

<details>
<summary><b>check_in</b> — 打卡动态表</summary>

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK AUTO_INCREMENT | 主键 |
| animal_id | BIGINT NOT NULL | 关联动物 (FK → animal.id ON DELETE CASCADE) |
| user_id | BIGINT NOT NULL | 打卡人 (FK → sys_user.id) |
| content | TEXT NOT NULL | 打卡内容 |
| images | VARCHAR(1000) | 图片 URL (逗号分隔) |
| mood | VARCHAR(20) | 心情: happy / neutral / worried |
| location | VARCHAR(100) | 打卡地点 |
| created_at | DATETIME | 打卡时间 |

</details>

---

## API 文档

**统一前缀：** `/api` &nbsp;|&nbsp; **响应格式：** `{ code, message, data }`

### 认证模块 `/api/auth`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 公开 |
| POST | `/api/auth/login` | 用户登录，返回 JWT | 公开 |
| GET | `/api/auth/me` | 获取当前用户信息 | 登录用户 |
| PUT | `/api/auth/password` | 修改密码 (需旧密码验证) | 登录用户 |
| DELETE | `/api/auth/account` | 注销账号 (级联删除打卡) | 登录用户 |

### 动物档案 `/api/animals`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/api/animals` | 分页查询 (支持 name 模糊 + type 筛选) | 登录用户 |
| GET | `/api/animals/{id}` | 查看详情 (含打卡时间轴) | 登录用户 |
| POST | `/api/animals` | 新增动物 | ADMIN |
| PUT | `/api/animals/{id}` | 修改档案 | ADMIN |
| DELETE | `/api/animals/{id}` | 删除档案 (级联删除打卡) | ADMIN |

### 打卡动态 `/api/checkins`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/checkins` | 发布打卡 | 登录用户 |
| GET | `/api/checkins/animal/{animalId}` | 动物打卡时间轴 (时间倒序) | 登录用户 |
| GET | `/api/checkins/my` | 我的打卡历史 | 登录用户 |
| DELETE | `/api/checkins/{id}` | 删除自己的打卡 | 登录用户 |
| DELETE | `/api/checkins/admin/{id}` | 管理员删除任意打卡 | ADMIN |
| GET | `/api/checkins/today-top` | 当日打卡最多的动物及路径 | 登录用户 |

### 文件上传 `/api/files`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/files/upload` | 上传图片 (返回 URL) | 登录用户 |
| GET | `/api/files/{filename}` | 访问已上传图片 | 公开 |

> 启动后端后，可访问 [http://localhost:8080/doc.html](http://localhost:8080/doc.html) 查看 Knife4j 在线 API 文档并直接调试接口。

---

## 快速开始

### 环境要求

| 组件 | 最低版本 |
|------|----------|
| JDK | 21 |
| Maven | 3.9+ |
| MySQL | 8.0 |
| Node.js | 18+ |

### 1. 克隆项目

```bash
git clone https://github.com/LiangZai-l/campus-animal-system.git
cd campus-animal-system
```

### 2. 初始化数据库

在 MySQL 中创建数据库并执行建表 SQL：

```sql
CREATE DATABASE campus_animal DEFAULT CHARACTER SET utf8mb4;

USE campus_animal;

-- 系统用户表
CREATE TABLE sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT 'BCrypt加密',
    real_name   VARCHAR(50)  COMMENT '真实姓名',
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT 'ADMIN/USER',
    phone       VARCHAR(20)  COMMENT '手机号',
    avatar_url  VARCHAR(255) COMMENT '头像URL',
    status      TINYINT      DEFAULT 1 COMMENT '1-正常 0-禁用',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 动物档案表
CREATE TABLE animal (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50)  NOT NULL COMMENT '暂定名字',
    type            VARCHAR(30)  NOT NULL COMMENT '猫/狗/鸟/其他',
    area            VARCHAR(100) NOT NULL COMMENT '常驻区域',
    description     TEXT         COMMENT '特征描述',
    cover_image     VARCHAR(255) COMMENT '封面照片URL',
    status          TINYINT      DEFAULT 1 COMMENT '1-在校 0-已离校',
    feeder_count    INT          DEFAULT 0 COMMENT '累计投喂次数',
    created_by      BIGINT       COMMENT '创建人ID',
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_type (type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动物档案表';

-- 打卡动态表
CREATE TABLE check_in (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    animal_id   BIGINT       NOT NULL COMMENT '关联动物ID',
    user_id     BIGINT       NOT NULL COMMENT '打卡人ID',
    content     TEXT         NOT NULL COMMENT '打卡内容',
    images      VARCHAR(1000) COMMENT '打卡图片(逗号分隔)',
    mood        VARCHAR(20)  COMMENT '心情: happy/neutral/worried',
    location    VARCHAR(100) COMMENT '打卡地点',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_animal_id (animal_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡动态表';
```

### 3. 修改数据库连接

编辑 `campus-animal-server/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_animal?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
```

### 4. 启动后端

```bash
cd campus-animal-server
mvn spring-boot:run
```

后端启动于 [http://localhost:8080](http://localhost:8080)，API 文档 [http://localhost:8080/doc.html](http://localhost:8080/doc.html)。

首次启动时 `DataInitializer` 会自动创建管理员账号。

### 5. 启动前端

```bash
cd campus-animal-web
npm install
npm run dev
```

前端启动于 [http://localhost:5173](http://localhost:5173)，API 请求自动代理到 `localhost:8080`。

---

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | `admin` | `admin123` |

> 管理员账号由 `DataInitializer` 在首次启动时自动创建。仅当数据库中无 admin 用户时才创建，不会覆盖已有密码。

---

## 界面截图

### 用户端

| 首页 | 动物详情 | 发布打卡 |
|:---:|:---:|:---:|
| 横幅 + 热门路径 + 卡片列表 | 档案信息 + 打卡时间轴 | 选择动物 + 填写内容 |

### 管理端

| 管理列表 | 新增/编辑动物 | 管理员删除打卡 |
|:---:|:---:|:---:|
| 表格 + 分页 + 增删改 | 表单 + 封面图上传 | 时间轴红色删除按钮 |

---

## 安全设计

| 措施 | 说明 |
|------|------|
| **BCrypt 密码加密** | 所有密码经 BCrypt 哈希后存储，永不存明文 |
| **JWT 无状态认证** | 每次请求在 Authorization 头携带 Bearer token，服务端不存 Session |
| **userId 后端获取** | 发布打卡等操作的 userId 从 SecurityContext 获取，不信任前端传入 |
| **方法级权限控制** | `@PreAuthorize("hasRole('ADMIN')")` 保护管理员接口 |
| **所有权校验** | 用户只能删除自己的打卡，管理员可删任意打卡 |
| **文件上传安全** | 扩展名从 Content-Type 推导（非用户文件名），UUID 防路径遍历 |
| **登录失败统一提示** | 不区分"用户不存在"和"密码错误"，防用户名枚举攻击 |
| **路由守卫** | 前端未登录跳登录页，非 ADMIN 无法访问 `/admin/*` |
| **401 自动处理** | Axios 拦截器捕获 401 → 清除本地状态 → 跳转登录页 |

---

## 开发约定

1. **分层架构：** Controller → Service → Mapper，单向依赖，不可跨层调用
2. **统一响应：** 所有接口返回 `Result<T>`（code + message + data）
3. **Controller 职责：** 只做参数接收、调用 Service、包装返回
4. **数据库查询：** 统一使用 `LambdaQueryWrapper`，避免字符串硬编码字段名
5. **VO/DTO/Entity 分离：** Entity 只映射表结构，DTO 接收参数，VO 自由聚合
6. **N+1 优化：** 批量查用户构建 usernameMap，2 次查询代替 N+1 次
7. **RESTful 风格：** URL 名词复数 + HTTP 方法语义，前缀 `/api`

---

## 许可证

本项目仅用于学习目的。

---

> 📅 创建日期：2026-06-10
