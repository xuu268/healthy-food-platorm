-- V2: 添加成分表AI系统相关表
-- 创建时间: 2026-03-23

-- ============================================
-- 1. 产品成分表
-- ============================================
CREATE TABLE `product_ingredient` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '成分ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `ingredient_name` varchar(100) NOT NULL COMMENT '成分名称',
  `ingredient_type` varchar(20) NOT NULL COMMENT '成分类型: MAIN-主料, ACCESSORY-辅料, ADDITIVE-添加剂, SEASONING-调味料',
  `quantity` decimal(10,2) NOT NULL COMMENT '含量',
  `unit` varchar(20) NOT NULL COMMENT '单位: g-克, ml-毫升, pcs-个',
  `safety_level` varchar(20) DEFAULT 'SAFE' COMMENT '安全等级: SAFE-安全, CAUTION-注意, WARNING-警告',
  `allergen_flag` tinyint DEFAULT '0' COMMENT '过敏原标志: 0-否, 1-是',
  `nutrition_data` json DEFAULT NULL COMMENT '营养成分数据',
  `source` varchar(100) DEFAULT NULL COMMENT '成分来源',
  `processing_method` varchar(50) DEFAULT NULL COMMENT '加工方式',
  `organic_certified` tinyint DEFAULT '0' COMMENT '有机认证: 0-否, 1-是',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_ingredient_name` (`ingredient_name`),
  KEY `idx_safety_level` (`safety_level`),
  KEY `idx_allergen_flag` (`allergen_flag`),
  CONSTRAINT `fk_ingredient_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品成分表';

-- ============================================
-- 2. 成分关键词表
-- ============================================
CREATE TABLE `ingredient_keyword` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关键词ID',
  `keyword` varchar(50) NOT NULL COMMENT '关键词',
  `category` varchar(20) NOT NULL COMMENT '分类: NUTRITION-营养, HEALTH-健康, DIET-饮食, ALLERGEN-过敏原, PREFERENCE-偏好',
  `description` text COMMENT '描述',
  `ai_generated` tinyint DEFAULT '1' COMMENT 'AI生成: 0-否, 1-是',
  `usage_count` int DEFAULT '0' COMMENT '使用次数',
  `popularity_score` decimal(3,2) DEFAULT '0.00' COMMENT '流行度评分',
  `created_by` bigint DEFAULT NULL COMMENT '创建者',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_keyword` (`keyword`),
  KEY `idx_category` (`category`),
  KEY `idx_popularity` (`popularity_score`),
  KEY `idx_usage_count` (`usage_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成分关键词表';

-- ============================================
-- 3. 产品关键词关联表
-- ============================================
CREATE TABLE `product_keyword_mapping` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `keyword_id` bigint NOT NULL COMMENT '关键词ID',
  `confidence_score` decimal(3,2) DEFAULT '1.00' COMMENT '置信度评分',
  `source` varchar(20) DEFAULT 'AI_ANALYSIS' COMMENT '来源: AI_ANALYSIS-AI分析, MANUAL-手动, USER_FEEDBACK-用户反馈',
  `match_reason` text COMMENT '匹配原因',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_keyword` (`product_id`, `keyword_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_keyword_id` (`keyword_id`),
  KEY `idx_confidence` (`confidence_score`),
  CONSTRAINT `fk_mapping_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_mapping_keyword` FOREIGN KEY (`keyword_id`) REFERENCES `ingredient_keyword` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='产品关键词关联表';

-- ============================================
-- 4. 消费者搜索偏好表
-- ============================================
CREATE TABLE `user_search_preference` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '偏好ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `keyword_id` bigint NOT NULL COMMENT '关键词ID',
  `preference_type` varchar(20) NOT NULL COMMENT '偏好类型: REQUIRE-必须, PREFER-偏好, AVOID-避免',
  `priority` int DEFAULT '1' COMMENT '优先级',
  `custom_weight` decimal(3,2) DEFAULT NULL COMMENT '自定义权重',
  `last_used_at` datetime DEFAULT NULL COMMENT '最后使用时间',
  `usage_count` int DEFAULT '0' COMMENT '使用次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_keyword` (`user_id`, `keyword_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_keyword_id` (`keyword_id`),
  KEY `idx_preference_type` (`preference_type`),
  KEY `idx_last_used` (`last_used_at`),
  CONSTRAINT `fk_preference_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_preference_keyword` FOREIGN KEY (`keyword_id`) REFERENCES `ingredient_keyword` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消费者搜索偏好表';

-- ============================================
-- 5. 成分分析结果表
-- ============================================
CREATE TABLE `ingredient_analysis` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分析ID',
  `product_id` bigint NOT NULL COMMENT '产品ID',
  `safety_score` decimal(3,2) DEFAULT NULL COMMENT '安全评分(0-1)',
  `nutrition_score` decimal(3,2) DEFAULT NULL COMMENT '营养评分(0-1)',
  `health_score` decimal(3,2) DEFAULT NULL COMMENT '健康评分(0-1)',
  `overall_score` decimal(3,2) DEFAULT NULL COMMENT '综合评分(0-1)',
  `allergen_summary` json DEFAULT NULL COMMENT '过敏原摘要',
  `nutrition_summary` json DEFAULT NULL COMMENT '营养摘要',
  `health_warnings` json DEFAULT NULL COMMENT '健康警告',
  `suitable_for` json DEFAULT NULL COMMENT '适合人群',
  `not_suitable_for` json DEFAULT NULL COMMENT '不适合人群',
  `ai_analysis_json` json DEFAULT NULL COMMENT 'AI分析原始数据',
  `analysis_version` varchar(20) DEFAULT '1.0' COMMENT '分析版本',
  `analyzed_at` datetime NOT NULL COMMENT '分析时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_analysis` (`product_id`),
  KEY `idx_safety_score` (`safety_score`),
  KEY `idx_nutrition_score` (`nutrition_score`),
  KEY `idx_overall_score` (`overall_score`),
  CONSTRAINT `fk_analysis_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成分分析结果表';

-- ============================================
-- 6. 成分知识库表
-- ============================================
CREATE TABLE `ingredient_knowledge` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '知识ID',
  `ingredient_name` varchar(100) NOT NULL COMMENT '成分名称',
  `scientific_name` varchar(200) DEFAULT NULL COMMENT '学名',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `description` text COMMENT '描述',
  `nutrition_facts` json DEFAULT NULL COMMENT '营养成分(每100g)',
  `health_benefits` json DEFAULT NULL COMMENT '健康益处',
  `potential_risks` json DEFAULT NULL COMMENT '潜在风险',
  `allergen_info` json DEFAULT NULL COMMENT '过敏原信息',
  `storage_conditions` text COMMENT '储存条件',
  `processing_methods` json DEFAULT NULL COMMENT '加工方式',
  `sustainability_score` decimal(3,2) DEFAULT NULL COMMENT '可持续性评分',
  `source_reliability` varchar(20) DEFAULT 'MEDIUM' COMMENT '来源可靠性: HIGH-高, MEDIUM-中, LOW-低',
  `references` json DEFAULT NULL COMMENT '参考文献',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ingredient_name` (`ingredient_name`),
  KEY `idx_category` (`category`),
  KEY `idx_sustainability` (`sustainability_score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='成分知识库表';

-- ============================================
-- 7. 插入初始关键词数据
-- ============================================
INSERT INTO `ingredient_keyword` (`keyword`, `category`, `description`, `ai_generated`, `popularity_score`) VALUES
-- 营养类关键词
('高蛋白', 'NUTRITION', '蛋白质含量较高，适合增肌和补充营养', 1, 0.95),
('低脂肪', 'NUTRITION', '脂肪含量较低，适合控制体重', 1, 0.92),
('低碳水', 'NUTRITION', '碳水化合物含量较低，适合控糖', 1, 0.88),
('高纤维', 'NUTRITION', '膳食纤维含量较高，促进消化', 1, 0.85),
('低糖', 'NUTRITION', '糖分含量较低，适合糖尿病患者', 1, 0.90),
('低钠', 'NUTRITION', '钠含量较低，适合高血压人群', 1, 0.82),
('高钙', 'NUTRITION', '钙含量较高，有助于骨骼健康', 1, 0.78),
('高铁', 'NUTRITION', '铁含量较高，有助于补血', 1, 0.75),

-- 健康类关键词
('健身餐', 'HEALTH', '适合健身人群的营养搭配', 1, 0.93),
('减肥餐', 'HEALTH', '低热量适合减重人群', 1, 0.91),
('控糖餐', 'HEALTH', '适合糖尿病或控糖人群', 1, 0.87),
('高血压友好', 'HEALTH', '低钠适合高血压人群', 1, 0.83),
('心脏健康', 'HEALTH', '富含不饱和脂肪酸，有益心脏', 1, 0.79),
('肠道健康', 'HEALTH', '富含益生菌或膳食纤维', 1, 0.76),
('免疫力提升', 'HEALTH', '富含维生素C等增强免疫力', 1, 0.81),

-- 饮食限制类关键词
('无麸质', 'DIET', '不含麸质，适合麸质不耐受人群', 1, 0.89),
('素食', 'DIET', '不含动物成分', 1, 0.86),
('纯素', 'DIET', '不含任何动物制品', 1, 0.84),
('无乳糖', 'DIET', '不含乳糖，适合乳糖不耐受', 1, 0.80),
('无添加糖', 'DIET', '不添加精制糖', 1, 0.88),
('无人工添加剂', 'DIET', '不含人工色素、防腐剂等', 1, 0.85),
('低嘌呤', 'DIET', '嘌呤含量低，适合痛风人群', 1, 0.77),

-- 过敏原类关键词
('无花生', 'ALLERGEN', '不含花生及制品', 1, 0.82),
('无坚果', 'ALLERGEN', '不含坚果及制品', 1, 0.80),
('无海鲜', 'ALLERGEN', '不含海鲜及制品', 1, 0.78),
('无鸡蛋', 'ALLERGEN', '不含鸡蛋及制品', 1, 0.76),
('无大豆', 'ALLERGEN', '不含大豆及制品', 1, 0.74),

-- 偏好类关键词
('有机', 'PREFERENCE', '有机认证食材', 1, 0.90),
('本地食材', 'PREFERENCE', '使用本地生产的食材', 1, 0.83),
('时令食材', 'PREFERENCE', '使用当季新鲜食材', 1, 0.81),
('清真', 'PREFERENCE', '符合清真食品标准', 1, 0.79),
('清真认证', 'PREFERENCE', '经过清真认证', 1, 0.77),
('手工制作', 'PREFERENCE', '手工制作非工业化生产', 1, 0.85),
('新鲜现做', 'PREFERENCE', '下单后新鲜制作', 1, 0.88);

-- ============================================
-- 8. 插入初始成分知识数据
-- ============================================
INSERT INTO `ingredient_knowledge` (`ingredient_name`, `scientific_name`, `category`, `description`, `nutrition_facts`, `health_benefits`, `potential_risks`, `allergen_info`, `sustainability_score`, `source_reliability`) VALUES
('鸡胸肉', 'Gallus gallus domesticus', '肉类', '低脂肪高蛋白的优质肉类', '{"calories": 165, "protein": 31, "fat": 3.6, "carbohydrates": 0, "cholesterol": 85}', '["高蛋白有助于肌肉生长", "低脂肪适合减肥", "富含B族维生素"]', '["过量摄入可能增加胆固醇", "烹饪不当可能滋生细菌"]', '{"common_allergen": false, "cross_reactivity": []}', 0.75, 'HIGH'),
('西兰花', 'Brassica oleracea var. italica', '蔬菜', '营养丰富的十字花科蔬菜', '{"calories": 34, "protein": 2.8, "fat": 0.4, "carbohydrates": 6.6, "fiber": 2.6}', '["富含维生素C和K", "抗氧化作用强", "有助于消化健康"]', '["甲状腺功能异常者需适量", "可能引起胀气"]', '{"common_allergen": false, "cross_reactivity": []}', 0.85, 'HIGH'),
('糙米', 'Oryza sativa', '谷物', '未经精加工的全谷物', '{"calories": 111, "protein": 2.6, "fat": 0.9, "carbohydrates": 23, "fiber": 1.8}', '["富含膳食纤维", "B族维生素丰富", "血糖反应较低"]', '["砷含量可能较高", "消化较慢可能不适"]', '{"common_allergen": false, "cross_reactivity": []}', 0.80, 'HIGH'),
('橄榄油', 'Olea europaea', '油脂', '健康的不饱和脂肪酸来源', '{"calories": 884, "fat": 100, "saturated_fat": 14, "monounsaturated_fat": 73}', '["富含单不饱和脂肪酸", "抗氧化作用", "有益心血管健康"]', '["高热量需控制用量", "烟点较低不适合高温油炸"]', '{"common_allergen": false, "cross_reactivity": []}', 0.70, 'HIGH'),
('豆腐', 'Glycine max', '豆制品', '大豆制成的植物蛋白食品', '{"calories": 76, "protein": 8.1, "fat": 4.8, "carbohydrates": 1.9}', '["优质植物蛋白", "富含异黄酮", "低胆固醇"]', '["大豆过敏者禁用", "痛风患者需适量"]', '{"common_allergen": true, "cross_reactivity": ["大豆", "花生"]}', 0.85, 'HIGH');

-- ============================================
-- 9. 更新系统配置
-- ============================================
INSERT INTO `system_config` (`config_key`, `config_value`, `config_type`, `description`, `is_public`) VALUES
('ingredient.ai_analysis_enabled', 'true', 'boolean', '启用成分AI分析', 1),
('ingredient.safety_threshold', '0.7', 'decimal', '安全评分阈值', 0),
('ingredient.nutrition_threshold', '0.6', 'decimal', '营养评分阈值', 0),
('ingredient.keyword_extraction_model', 'default', 'string', '关键词提取模型', 0),
('ingredient.allergen_check_enabled', 'true', 'boolean', '启用过敏原检查', 1),
('ingredient.nutrition_calculation_enabled', 'true', 'boolean', '启用营养计算', 1),
('ingredient.health_warning_enabled', 'true', 'boolean', '启用健康警告', 1),
('ingredient.recommendation_algorithm', 'hybrid', 'string', '推荐算法类型', 0),
('ingredient.min_confidence_score', '0.8', 'decimal', '最小置信度评分', 0),
('ingredient.default_analysis_version', '1.0', 'string', '默认分析版本', 0);

-- ============================================
-- 10. 为现有产品添加示例成分数据
-- ============================================
-- 注意：实际应用中应由商家填写，这里仅为示例
INSERT INTO `product_ingredient` (`product_id`, `ingredient_name`, `ingredient_type`, `quantity`, `unit`, `safety_level`, `allergen_flag`, `nutrition_data`) 
SELECT 
  p.id,
  CASE 
    WHEN p.category = '健身餐' THEN '鸡胸肉'
    WHEN p.category = '素食餐' THEN '豆腐'
    WHEN p.category = '轻食沙拉' THEN '西兰花'
    ELSE '糙米'
  END,
  'MAIN',
  150.00,
  'g',
  'SAFE',
  0,
  CASE 
    WHEN p.category = '健身餐' THEN '{"calories": 165, "protein": 31, "fat": 3.6}'
    WHEN p.category = '素食餐' THEN '{"calories": 76, "protein": 8.1, "fat": 4.8}'
    WHEN p.category = '轻食沙拉' THEN '{"calories": 34, "protein": 2.8, "fat": 0.4}'
    ELSE '{"calories": 111, "protein": 2.6, "fat": 0.9}'
  END
FROM `product` p
WHERE p.status = 1
LIMIT 20;

-- 记录迁移完成
INSERT INTO `operation_log` (`operation`, `method`, `url`, `params`, `status`) 
VALUES ('数据库迁移-V2成分表系统', 'SQL', 'migration/V2__add_ingredient_tables.sql', '{"tables_added": 6, "keywords_inserted": 35, "knowledge_inserted": 5}', 1);