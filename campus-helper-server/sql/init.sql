-- ============================================================
-- 校园帮 · 数据库初始化脚本
-- 说明：先创建数据库，再执行本脚本建表
-- ============================================================

CREATE DATABASE IF NOT EXISTS campus_helper
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE campus_helper;

-- ============================================================
-- 1. 用户表
-- ============================================================
CREATE TABLE `user` (
    `id`           BIGINT       NOT NULL COMMENT '主键',
    `username`     VARCHAR(32)  NOT NULL COMMENT '用户名',
    `password`     VARCHAR(128) NOT NULL COMMENT '密码(bcrypt)',
    `nickname`     VARCHAR(32)  DEFAULT NULL COMMENT '昵称',
    `avatar`       VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `phone`        VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `email`        VARCHAR(64)  DEFAULT NULL COMMENT '邮箱',
    `gender`       TINYINT      DEFAULT 0  COMMENT '性别:0未知 1男 2女',
    `user_type`    TINYINT      DEFAULT 1  COMMENT '类型:0管理员 1普通用户',
    `status`       TINYINT      DEFAULT 0  COMMENT '状态:0正常 1禁用',
    `real_name`    VARCHAR(32)  DEFAULT NULL COMMENT '真实姓名',
    `student_id`   VARCHAR(32)  DEFAULT NULL COMMENT '学号',
    `college`      VARCHAR(64)  DEFAULT NULL COMMENT '学院',
    `major`        VARCHAR(64)  DEFAULT NULL COMMENT '专业',
    `grade`        VARCHAR(16)  DEFAULT NULL COMMENT '年级',
    `credit_score` INT          DEFAULT 100 COMMENT '信用分',
    `deleted`      TINYINT      DEFAULT 0  COMMENT '逻辑删除',
    `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_time`  DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone`    (`phone`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_college`   (`college`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 2. 分类字典表
-- ============================================================
CREATE TABLE `category` (
    `id`          BIGINT       NOT NULL COMMENT '主键',
    `name`        VARCHAR(32)  NOT NULL COMMENT '分类名称',
    `parent_id`   BIGINT       DEFAULT 0  COMMENT '父分类ID(0=顶级)',
    `sort`        INT          DEFAULT 0  COMMENT '排序值',
    `icon`        VARCHAR(64)  DEFAULT NULL COMMENT '图标',
    `status`      TINYINT      DEFAULT 1  COMMENT '状态:0禁用 1启用',
    `deleted`     TINYINT      DEFAULT 0  COMMENT '逻辑删除',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_sort`      (`sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类字典表';

-- ============================================================
-- 3. 任务表
-- ============================================================
CREATE TABLE `task` (
    `id`             BIGINT       NOT NULL COMMENT '主键',
    `title`          VARCHAR(128) NOT NULL COMMENT '任务标题',
    `description`    TEXT         DEFAULT NULL COMMENT '任务描述',
    `category_id`    BIGINT       DEFAULT NULL COMMENT '分类ID',
    `user_id`        BIGINT       NOT NULL COMMENT '发布者ID',
    `price`          DECIMAL(10,2) DEFAULT 0.00 COMMENT '报酬(元)',
    `contact_phone`  VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    `address`        VARCHAR(255) DEFAULT NULL COMMENT '地点',
    `status`         TINYINT      DEFAULT 0  COMMENT '状态:0待接单 1进行中 2已完成 3已取消',
    `urgent`         TINYINT      DEFAULT 0  COMMENT '加急:0普通 1加急',
    `deadline`       DATETIME     DEFAULT NULL COMMENT '截止时间',
    `view_count`     INT          DEFAULT 0  COMMENT '浏览次数',
    `deleted`        TINYINT      DEFAULT 0  COMMENT '逻辑删除',
    `create_time`    DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_time`    DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id`     (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status`      (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- ============================================================
-- 4. 接单记录表
-- ============================================================
CREATE TABLE `task_accept` (
    `id`            BIGINT       NOT NULL COMMENT '主键',
    `task_id`       BIGINT       NOT NULL COMMENT '任务ID',
    `user_id`       BIGINT       NOT NULL COMMENT '接单者ID',
    `status`        TINYINT      DEFAULT 0  COMMENT '状态:0进行中 1已完成 2已取消',
    `accept_time`   DATETIME     DEFAULT NULL COMMENT '接单时间',
    `complete_time` DATETIME     DEFAULT NULL COMMENT '完成时间',
    `deleted`       TINYINT      DEFAULT 0  COMMENT '逻辑删除',
    `create_time`   DATETIME     DEFAULT NULL COMMENT '创建时间',
    `update_time`   DATETIME     DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_user` (`task_id`, `user_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='接单记录表';
