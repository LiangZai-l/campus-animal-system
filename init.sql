-- ============================================
-- 校园流浪动物图鉴与动态打卡系统 — 数据库初始化
-- ============================================

-- 1. 创建数据库
DROP DATABASE IF EXISTS campus_animal;
CREATE DATABASE campus_animal
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE campus_animal;

-- 2. 系统用户表
CREATE TABLE sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    real_name   VARCHAR(50)  COMMENT '真实姓名',
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN/USER',
    phone       VARCHAR(20)  COMMENT '手机号',
    avatar_url  VARCHAR(255) COMMENT '头像URL',
    status      TINYINT      DEFAULT 1 COMMENT '1-正常 0-禁用',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 3. 动物档案表
CREATE TABLE animal (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(50)  NOT NULL COMMENT '暂定名字',
    type            VARCHAR(30)  NOT NULL COMMENT '动物类型：猫/狗/鸟/其他',
    area            VARCHAR(100) NOT NULL COMMENT '常驻区域',
    description     TEXT         COMMENT '特征描述',
    cover_image     VARCHAR(255) COMMENT '封面照片URL',
    status          TINYINT      DEFAULT 1 COMMENT '1-在校 0-已离校',
    feeder_count    INT          DEFAULT 0 COMMENT '累计投喂次数',
    created_by      BIGINT       COMMENT '创建人ID',
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_name (name),
    INDEX idx_type (type),
    INDEX idx_area (area)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动物档案表';

-- 4. 打卡动态表
CREATE TABLE check_in (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    animal_id   BIGINT       NOT NULL COMMENT '关联动物ID',
    user_id     BIGINT       NOT NULL COMMENT '打卡人ID',
    content     TEXT         NOT NULL COMMENT '打卡内容',
    images      VARCHAR(1000) COMMENT '打卡图片（多个逗号分隔）',
    mood        VARCHAR(20)  COMMENT '心情：happy/neutral/worried',
    location    VARCHAR(100) COMMENT '打卡地点',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_animal_id (animal_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (animal_id) REFERENCES animal(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡动态表';

-- 5. 插入初始化管理员账号（密码 admin123，用 BCrypt 加密后的密文）
--    项目启动后会用 Java 代码正式初始化，这里先占位
INSERT INTO sys_user (username, password, real_name, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5Eh', '系统管理员', 'ADMIN');

-- 验证
SELECT '数据库初始化完成！' AS message;
SHOW TABLES;
