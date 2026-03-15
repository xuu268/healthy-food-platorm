  `type` varchar(50) NOT NULL COMMENT '积分类型',
  `amount` int NOT NULL COMMENT '积分数量',
  `balance` int NOT NULL COMMENT '积分余额',
  `description` varchar(200) NOT NULL COMMENT '描述',
  `reference_type` varchar(50) DEFAULT NULL COMMENT '关联类型',
  `reference_id` varchar(100) DEFAULT NULL COMMENT '关联ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分记录表';

-- 支付记录表
CREATE TABLE `payment_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `order_id` varchar(32) NOT NULL COMMENT '订单ID',
  `payment_method` varchar(50) NOT NULL COMMENT '支付方式',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '交易号',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `currency` varchar(10) DEFAULT 'CNY' COMMENT '货币',
  `status` tinyint NOT NULL COMMENT '支付状态 0-待支付 1-支付成功 2-支付失败 3-退款',
  `payer_id` bigint DEFAULT NULL COMMENT '支付者ID',
  `payer_info` json DEFAULT NULL COMMENT '支付者信息',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `notify_data` text COMMENT '支付通知数据',
  `refund_amount` decimal(10,2) DEFAULT '0.00' COMMENT '退款金额',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transaction_id` (`transaction_id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_payer_id` (`payer_id`),
  KEY `idx_status` (`status`),
  KEY `idx_payment_time` (`payment_time`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- 系统配置表
CREATE TABLE `system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `config_type` varchar(50) DEFAULT 'string' COMMENT '配置类型',
  `description` varchar(200) DEFAULT NULL COMMENT '描述',
  `is_public` tinyint DEFAULT '0' COMMENT '是否公开',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_is_public` (`is_public`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 操作日志表
CREATE TABLE `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `operation` varchar(100) NOT NULL COMMENT '操作',
  `method` varchar(10) NOT NULL COMMENT '请求方法',
  `url` varchar(500) NOT NULL COMMENT '请求URL',
  `params` text COMMENT '请求参数',
  `ip` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` text COMMENT '用户代理',
  `duration` int DEFAULT NULL COMMENT '耗时(ms)',
  `status` tinyint DEFAULT '1' COMMENT '状态 0-失败 1-成功',
  `error_message` text COMMENT '错误信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation` (`operation`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 插入初始数据
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`, `is_public`) VALUES
('app.name', '健康餐饮AI平台', 'string', '应用名称', 1),
('app.version', '1.0.0', 'string', '应用版本', 1),
('app.description', '智能健康餐饮生态平台', 'string', '应用描述', 1),
('points.content_publish', '10', 'int', '发布内容积分', 1),
('points.content_quality', '50', 'int', '优质内容积分', 1),
('points.content_viral', '200', 'int', '爆款内容积分', 1),
('points.shop_connection', '100', 'int', '商家对接积分', 1),
('points.shop_review', '50', 'int', '商家评价积分', 1),
('points.shop_repurchase', '200', 'int', '商家复购积分', 1),
('points.invite_creator', '500', 'int', '邀请创作者积分', 1),
('creator.commission_rate', '0.2', 'decimal', '创作者分成比例', 0),
('health.daily_calorie_target', '2000', 'int', '每日热量目标', 1),
('health.protein_target', '60', 'decimal', '蛋白质目标(g)', 1),
('health.carb_target', '300', 'decimal', '碳水目标(g)', 1),
('health.fat_target', '65', 'decimal', '脂肪目标(g)', 1),
('shop.verification_fee', '99', 'decimal', '商家认证费用', 0),
('subscription.free_trial_days', '14', 'int', '免费试用天数', 1),
('subscription.basic_monthly_price', '199', 'decimal', '基础版月费', 1),
('subscription.pro_monthly_price', '499', 'decimal', '专业版月费', 1),
('subscription.enterprise_monthly_price', '999', 'decimal', '企业版月费', 1);

-- 创建管理员用户 (密码: admin123)
INSERT INTO `user` (`username`, `phone`, `email`, `password`, `nickname`, `avatar`, `status`, `level`) VALUES
('admin', '13800138000', 'admin@healthyfood.com', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '管理员', NULL, 1, 99);

-- 创建测试商家
INSERT INTO `shop` (`name`, `description`, `address`, `phone`, `business_hours`, `avg_price`, `status`, `is_verified`) VALUES
('健康轻食坊', '专注健康轻食，低卡低脂', '大学城美食街18号', '13800138001', '10:00-22:00', 35.00, 1, 1),
('营养早餐店', '提供营养均衡的早餐', '工厂区生活广场3号', '13800138002', '06:00-11:00', 15.00, 1, 1),
('健身餐专门店', '为健身人群定制餐食', '大学城体育馆旁', '13800138003', '11:00-21:00', 45.00, 1, 1);

-- 创建测试商品
INSERT INTO `product` (`shop_id`, `name`, `description`, `price`, `calories`, `protein`, `carbohydrates`, `fat`, `health_score`, `status`) VALUES
(1, '鸡胸肉沙拉', '新鲜蔬菜搭配烤鸡胸肉', 28.00, 320, 25.5, 12.3, 8.2, 4.5, 1),
(1, '牛油果三明治', '全麦面包配牛油果和蔬菜', 22.00, 280, 8.5, 35.2, 12.1, 4.2, 1),
(2, '营养早餐套餐', '鸡蛋+牛奶+全麦面包+水果', 18.00, 350, 18.2, 42.5, 10.3, 4.3, 1),
(2, '燕麦粥套餐', '燕麦粥+坚果+水果', 12.00, 250, 6.5, 38.2, 5.8, 4.6, 1),
(3, '增肌蛋白餐', '高蛋白鸡胸肉+糙米+蔬菜', 38.00, 420, 35.8, 45.2, 8.5, 4.4, 1),
(3, '减脂轻食餐', '低卡鱼肉+蔬菜沙拉', 32.00, 280, 22.5, 15.8, 6.2, 4.7, 1);

COMMIT;