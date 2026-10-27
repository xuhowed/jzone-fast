-- ============================================================================
-- JZone-Fast Demo 模块初始化脚本（商品管理示例）
-- 执行前提：已经执行过 init.sql，并且数据库中已存在基础系统表。
-- ============================================================================

SET NAMES utf8mb4;

-- ----------------------------
-- 商品表
-- ----------------------------
DROP TABLE IF EXISTS `jz_demo_product`;
CREATE TABLE `jz_demo_product` (
    `id`           BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `product_name` VARCHAR(100)    NOT NULL COMMENT '商品名称',
    `product_code` VARCHAR(50)     NOT NULL COMMENT '商品编号',
    `category`     VARCHAR(50)     DEFAULT NULL COMMENT '分类',
    `price`        DECIMAL(10, 2)  DEFAULT NULL COMMENT '价格',
    `stock`        INT             DEFAULT NULL COMMENT '库存数量',
    `status`       TINYINT         NOT NULL DEFAULT 1 COMMENT '状态 1上架 0下架',
    `remark`       VARCHAR(500)    DEFAULT NULL COMMENT '备注',
    `create_time`  DATETIME        DEFAULT NULL,
    `update_time`  DATETIME        DEFAULT NULL,
    `create_by`    VARCHAR(64)     DEFAULT NULL,
    `update_by`    VARCHAR(64)     DEFAULT NULL,
    UNIQUE KEY `uk_product_code` (`product_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品表(示例)';

-- ----------------------------
-- 示例数据
-- ----------------------------
INSERT INTO `jz_demo_product`
(`product_name`, `product_code`, `category`, `price`, `stock`, `status`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES
('机械键盘 K8 Pro',   'KB-001', '数码外设', 599.00,  120, 1, '无线/有线双模',   NOW(), NOW(), 'system', 'system'),
('罗技 G502 鼠标',    'MS-002', '数码外设', 299.00,  80,  1, '游戏鼠标',         NOW(), NOW(), 'system', 'system'),
('Dell U2723QE 显示器','MN-003', '显示设备', 3999.00, 25,  1, '4K 60Hz',          NOW(), NOW(), 'system', 'system'),
('Sony WH-1000XM5',   'HP-004', '音频设备', 2299.00, 45,  1, '头戴降噪耳机',     NOW(), NOW(), 'system', 'system'),
('Apple Magic Trackpad','TP-005','输入设备',  999.00,  30,  0, '已下架示例',       NOW(), NOW(), 'system', 'system'),
('海康 C2000 Pro 1TB','HD-006', '存储设备',  459.00,  200, 1, 'NVMe 固态硬盘',    NOW(), NOW(), 'system', 'system'),
('海盗船 DDR5 16G',   'MM-007', '内存',      499.00,  150, 1, 'DDR5 5600MHz',     NOW(), NOW(), 'system', 'system'),
('绿联 Type-C 扩展坞','HB-008', '配件',      199.00,  300, 1, '7合1扩展坞',       NOW(), NOW(), 'system', 'system');

-- ============================================================================
-- 菜单与权限
-- ============================================================================

-- 1) 清理旧数据（幂等）
DELETE FROM `jz_sys_role_menu` WHERE `menu_id` >= 10 AND `menu_id` <= 17;
DELETE FROM `jz_sys_menu`      WHERE `id`      >= 10 AND `id`      <= 17;

-- 2) 插入商品示例导航菜单与按钮权限 (统一在 jz_sys_menu)
INSERT INTO `jz_sys_menu`
(`id`, `parent_id`, `menu_name`, `menu_type`, `path`, `component`, `permission_code`, `icon`, `sort`, `status`, `hidden`, `keep_alive`, `create_time`, `update_time`, `create_by`, `update_by`)
VALUES
(10, 0,  '示例模块', 1, '/demo',         NULL,                 NULL,                 'GoodsFilled', 10, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(11, 10, '商品管理', 2, '/demo/product', 'demo/product/index', 'demo:product:query', 'Goods',        1, 1, 0, 1, NOW(), NOW(), 'system', 'system'),
(12, 11, '商品查询', 3, NULL,            NULL,                 'demo:product:query',  NULL,          1, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(13, 11, '商品新增', 3, NULL,            NULL,                 'demo:product:create', NULL,          2, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(14, 11, '商品编辑', 3, NULL,            NULL,                 'demo:product:update', NULL,          3, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(15, 11, '商品删除', 3, NULL,            NULL,                 'demo:product:delete', NULL,          4, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(16, 11, '商品导出', 3, NULL,            NULL,                 'demo:product:export', NULL,          5, 1, 0, 0, NOW(), NOW(), 'system', 'system'),
(17, 11, '商品导入', 3, NULL,            NULL,                 'demo:product:import', NULL,          6, 1, 0, 0, NOW(), NOW(), 'system', 'system');

-- 3) 超级管理员绑定所有 demo 权限
INSERT INTO `jz_sys_role_menu` (`role_id`, `menu_id`, `create_time`, `create_by`)
SELECT 1, id, NOW(), 'system' FROM `jz_sys_menu` WHERE `id` >= 10 AND `id` <= 17;
