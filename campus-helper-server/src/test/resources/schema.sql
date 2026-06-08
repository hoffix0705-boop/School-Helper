CREATE TABLE IF NOT EXISTS `user` (
    `id`           BIGINT       NOT NULL,
    `username`     VARCHAR(32)  NOT NULL,
    `password`     VARCHAR(128) NOT NULL,
    `nickname`     VARCHAR(32)  DEFAULT NULL,
    `avatar`       VARCHAR(255) DEFAULT NULL,
    `phone`        VARCHAR(20)  DEFAULT NULL,
    `email`        VARCHAR(64)  DEFAULT NULL,
    `gender`       TINYINT      DEFAULT 0,
    `user_type`    TINYINT      DEFAULT 1,
    `status`       TINYINT      DEFAULT 0,
    `real_name`    VARCHAR(32)  DEFAULT NULL,
    `student_id`   VARCHAR(32)  DEFAULT NULL,
    `college`      VARCHAR(64)  DEFAULT NULL,
    `major`        VARCHAR(64)  DEFAULT NULL,
    `grade`        VARCHAR(16)  DEFAULT NULL,
    `credit_score` INT          DEFAULT 100,
    `deleted`      TINYINT      DEFAULT 0,
    `create_time`  DATETIME     DEFAULT NULL,
    `update_time`  DATETIME     DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `category` (
    `id`          BIGINT       NOT NULL,
    `name`        VARCHAR(32)  NOT NULL,
    `parent_id`   BIGINT       DEFAULT 0,
    `sort`        INT          DEFAULT 0,
    `icon`        VARCHAR(64)  DEFAULT NULL,
    `status`      TINYINT      DEFAULT 1,
    `deleted`     TINYINT      DEFAULT 0,
    `create_time` DATETIME     DEFAULT NULL,
    `update_time` DATETIME     DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `task` (
    `id`             BIGINT       NOT NULL,
    `title`          VARCHAR(128) NOT NULL,
    `description`    TEXT         DEFAULT NULL,
    `category_id`    BIGINT       DEFAULT NULL,
    `user_id`        BIGINT       NOT NULL,
    `price`          DECIMAL(10,2) DEFAULT 0.00,
    `contact_phone`  VARCHAR(20)  DEFAULT NULL,
    `address`        VARCHAR(255) DEFAULT NULL,
    `status`         TINYINT      DEFAULT 0,
    `urgent`         TINYINT      DEFAULT 0,
    `deadline`       DATETIME     DEFAULT NULL,
    `view_count`     INT          DEFAULT 0,
    `deleted`        TINYINT      DEFAULT 0,
    `create_time`    DATETIME     DEFAULT NULL,
    `update_time`    DATETIME     DEFAULT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `task_accept` (
    `id`            BIGINT       NOT NULL,
    `task_id`       BIGINT       NOT NULL,
    `user_id`       BIGINT       NOT NULL,
    `status`        TINYINT      DEFAULT 0,
    `accept_time`   DATETIME     DEFAULT NULL,
    `complete_time` DATETIME     DEFAULT NULL,
    `deleted`       TINYINT      DEFAULT 0,
    `create_time`   DATETIME     DEFAULT NULL,
    `update_time`   DATETIME     DEFAULT NULL,
    PRIMARY KEY (`id`)
);