-- JZone-Fast 初始化脚本
-- 执行前请先创建数据库: CREATE DATABASE `jzone-fast` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `jz_sys_user`;
CREATE TABLE `jz_sys_user` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `username`    VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(128) NOT NULL COMMENT '密码(BCrypt)',
    `nickname`    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    `email`       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
    `create_time` DATETIME     DEFAULT NULL,
    `update_time` DATETIME     DEFAULT NULL,
    `create_by`   VARCHAR(64)  DEFAULT NULL,
    `update_by`   VARCHAR(64)  DEFAULT NULL,
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户表';

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `jz_sys_role`;
CREATE TABLE `jz_sys_role` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `role_name`   VARCHAR(64)  NOT NULL COMMENT '角色名称',
    `role_code`   VARCHAR(64)  NOT NULL COMMENT '角色编码',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
    `remark`      VARCHAR(256) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     DEFAULT NULL,
    `update_time` DATETIME     DEFAULT NULL,
    `create_by`   VARCHAR(64)  DEFAULT NULL,
    `update_by`   VARCHAR(64)  DEFAULT NULL,
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色表';

-- ----------------------------
-- 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `jz_sys_user_role`;
CREATE TABLE `jz_sys_user_role` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `user_id`     BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
    `role_id`     BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `create_time` DATETIME        DEFAULT NULL,
    `update_time` DATETIME        DEFAULT NULL,
    `create_by`   VARCHAR(64)     DEFAULT NULL,
    `update_by`   VARCHAR(64)     DEFAULT NULL,
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户角色关联表';

-- ----------------------------
-- 菜单权限表 (统一目录、菜单、按钮树)
-- ----------------------------
DROP TABLE IF EXISTS `jz_sys_menu`;
CREATE TABLE `jz_sys_menu` (
    `id`              BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `parent_id`       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '父菜单ID',
    `menu_name`       VARCHAR(64)  NOT NULL COMMENT '菜单名称',
    `menu_type`       TINYINT      NOT NULL DEFAULT 1 COMMENT '菜单类型 1目录 2菜单 3按钮',
    `path`            VARCHAR(256) DEFAULT NULL COMMENT '路由路径',
    `component`       VARCHAR(256) DEFAULT NULL COMMENT '组件路径',
    `permission_code` VARCHAR(128) DEFAULT NULL COMMENT '关联权限标识',
    `icon`            VARCHAR(64)  DEFAULT NULL COMMENT '图标',
    `sort`            INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
    `hidden`          TINYINT      NOT NULL DEFAULT 0 COMMENT '0显示 1隐藏',
    `keep_alive`      TINYINT      NOT NULL DEFAULT 0 COMMENT '0不缓存 1缓存',
    `create_time`     DATETIME     DEFAULT NULL,
    `update_time`     DATETIME     DEFAULT NULL,
    `create_by`       VARCHAR(64)  DEFAULT NULL,
    `update_by`       VARCHAR(64)  DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统菜单权限表';

-- ----------------------------
-- 角色菜单关联表 (标准 RBAC 授权)
-- ----------------------------
DROP TABLE IF EXISTS `jz_sys_role_menu`;
CREATE TABLE `jz_sys_role_menu` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `role_id`     BIGINT UNSIGNED NOT NULL COMMENT '角色ID',
    `menu_id`     BIGINT UNSIGNED NOT NULL COMMENT '菜单/权限ID',
    `create_time` DATETIME        DEFAULT NULL,
    `update_time` DATETIME        DEFAULT NULL,
    `create_by`   VARCHAR(64)     DEFAULT NULL,
    `update_by`   VARCHAR(64)     DEFAULT NULL,
    UNIQUE KEY `uk_role_menu` (`role_id`, `menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色菜单关联表';

-- ----------------------------
-- 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `jz_sys_dict_type`;
CREATE TABLE `jz_sys_dict_type` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `dict_name`   VARCHAR(64)  NOT NULL COMMENT '字典名称',
    `dict_type`   VARCHAR(64)  NOT NULL COMMENT '字典类型',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
    `remark`      VARCHAR(256) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     DEFAULT NULL,
    `update_time` DATETIME     DEFAULT NULL,
    `create_by`   VARCHAR(64)  DEFAULT NULL,
    `update_by`   VARCHAR(64)  DEFAULT NULL,
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';

-- ----------------------------
-- 字典项表
-- ----------------------------
DROP TABLE IF EXISTS `jz_sys_dict_item`;
CREATE TABLE `jz_sys_dict_item` (
    `id`          BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `dict_type`   VARCHAR(64)  NOT NULL COMMENT '字典类型',
    `item_label`  VARCHAR(64)  NOT NULL COMMENT '字典标签',
    `item_value`  VARCHAR(64)  NOT NULL COMMENT '字典值',
    `sort`        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `status`      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态 1正常 0禁用',
    `remark`      VARCHAR(256) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME     DEFAULT NULL,
    `update_time` DATETIME     DEFAULT NULL,
    `create_by`   VARCHAR(64)  DEFAULT NULL,
    `update_by`   VARCHAR(64)  DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典项表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================
-- 初始数据
-- ============================

-- 超级管理员 admin/admin (BCrypt)
INSERT INTO `jz_sys_user` (`id`, `username`, `password`, `nickname`, `status`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES (1, 'admin', '$2a$10$iUtFQ9LY9KtPaJVg8gd3DexsWPo5Z.TIkdel0M.4sTB/u2d5s7IGu', '超级管理员', 1, NOW(), NOW(), 'system', 'system');

-- 测试用户 test/123456 (BCrypt)
INSERT INTO `jz_sys_user` (`id`, `username`, `password`, `nickname`, `status`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES (2, 'test', '$2a$10$u4AFrvLk.9Uc765Wt42KhOgjzq0xWIUQAjhwrzaGFpfbdwKp7oC1C', '测试用户', 1, NOW(), NOW(), 'system', 'system');

-- 超级管理员角色
INSERT INTO `jz_sys_role` (`id`, `role_name`, `role_code`, `sort`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES (1, '超级管理员', 'admin', 1, 1, '系统内置超级管理员', NOW(), NOW(), 'system', 'system');

-- 测试角色
INSERT INTO `jz_sys_role` (`id`, `role_name`, `role_code`, `sort`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES (2, '测试角色', 'test', 2, 1, '系统内置测试角色', NOW(), NOW(), 'system', 'system');

-- 绑定用户角色
INSERT INTO `jz_sys_user_role` (`user_id`, `role_id`, `create_time`, `create_by`) VALUES
(1, 1, NOW(), 'system'),
(2, 2, NOW(), 'system');

-- 系统菜单与权限数据 (统一在 jz_sys_menu 表)
-- 1. 目录与页面菜单 (menu_type: 1目录, 2菜单)
INSERT INTO `jz_sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission_code`, `icon`, `sort`, `status`, `hidden`, `keep_alive`, `create_time`, `update_time`, `create_by`, `update_by`) VALUES
(1, 0, '系统管理', 1, '/system',      NULL,                    NULL,             'Setting',    1, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(2, 1, '用户管理', 2, '/system/user', 'system/user/index',     'sys:user:query', 'User',       1, 1, 0, 1, NOW(), NOW(), 'system', 'system'),
(3, 1, '角色管理', 2, '/system/role', 'system/role/index',     'sys:role:query', 'UserFilled', 2, 1, 0, 1, NOW(), NOW(), 'system', 'system'),
(4, 1, '菜单管理', 2, '/system/menu', 'system/menu/index',     'sys:menu:query', 'Menu',       3, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(5, 1, '字典管理', 2, '/system/dict', 'system/dict/index',     'sys:dict:query', 'Collection', 4, 1, 0, 1, NOW(), NOW(), 'system', 'system');

-- 2. 按钮权限 (menu_type: 3按钮)
INSERT INTO `jz_sys_menu` (`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission_code`, `icon`, `sort`, `status`, `hidden`, `keep_alive`, `create_time`, `update_time`, `create_by`, `update_by`) VALUES
-- 用户管理按钮
(101, 2, '用户查询', 3, NULL, NULL, 'sys:user:query',  NULL, 1, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(102, 2, '用户新增', 3, NULL, NULL, 'sys:user:create', NULL, 2, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(103, 2, '用户编辑', 3, NULL, NULL, 'sys:user:update', NULL, 3, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(104, 2, '用户删除', 3, NULL, NULL, 'sys:user:delete', NULL, 4, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
-- 角色管理按钮
(105, 3, '角色查询', 3, NULL, NULL, 'sys:role:query',  NULL, 1, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(106, 3, '角色新增', 3, NULL, NULL, 'sys:role:create', NULL, 2, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(107, 3, '角色编辑', 3, NULL, NULL, 'sys:role:update', NULL, 3, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(108, 3, '角色删除', 3, NULL, NULL, 'sys:role:delete', NULL, 4, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
-- 菜单管理按钮
(109, 4, '菜单查询', 3, NULL, NULL, 'sys:menu:query',  NULL, 1, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(110, 4, '菜单新增', 3, NULL, NULL, 'sys:menu:create', NULL, 2, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(111, 4, '菜单编辑', 3, NULL, NULL, 'sys:menu:update', NULL, 3, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(112, 4, '菜单删除', 3, NULL, NULL, 'sys:menu:delete', NULL, 4, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
-- 字典管理按钮
(113, 5, '字典查询', 3, NULL, NULL, 'sys:dict:query',  NULL, 1, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(114, 5, '字典新增', 3, NULL, NULL, 'sys:dict:create', NULL, 2, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(115, 5, '字典编辑', 3, NULL, NULL, 'sys:dict:update', NULL, 3, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(116, 5, '字典删除', 3, NULL, NULL, 'sys:dict:delete', NULL, 4, 1, 0, 0, NOW(), NOW(), 'system', 'system');

-- 超级管理员赋予所有菜单与权限
INSERT INTO `jz_sys_role_menu` (`role_id`, `menu_id`, `create_time`, `create_by`)
SELECT 1, id, NOW(), 'system' FROM `jz_sys_menu`;

-- 测试角色赋予基础查询与查看权限（包括目录和页面）
INSERT INTO `jz_sys_role_menu` (`role_id`, `menu_id`, `create_time`, `create_by`)
SELECT 2, id, NOW(), 'system' FROM `jz_sys_menu` WHERE `id` IN (1, 2, 3, 4, 5, 101, 105, 109, 113);

-- 示例字典数据
INSERT INTO `jz_sys_dict_type` (`dict_name`, `dict_type`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES ('用户状态', 'user_status', 1, '用户状态字典', NOW(), NOW(), 'system', 'system');

INSERT INTO `jz_sys_dict_item` (`dict_type`, `item_label`, `item_value`, `sort`, `status`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES
('user_status', '正常', '1', 1, 1, NOW(), NOW(), 'system', 'system'),
('user_status', '禁用', '0', 2, 1, NOW(), NOW(), 'system', 'system');
