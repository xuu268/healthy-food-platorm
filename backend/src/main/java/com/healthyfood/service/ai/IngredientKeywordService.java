package com.healthyfood.service.ai;

import com.healthyfood.entity.IngredientKeyword;
import com.healthyfood.entity.ProductIngredient;
import com.healthyfood.repository.IngredientKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 成分关键词服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IngredientKeywordService {
    
    private final IngredientKeywordRepository keywordRepository;
    
    // 预定义的关键词映射规则
    private static final Map<String, List<String>> NUTRITION_KEYWORD_RULES = new HashMap<>();
    private static final Map<String, List<String>> HEALTH_KEYWORD_RULES = new HashMap<>();
    
    static {
        // 营养关键词规则
        NUTRITION_KEYWORD_RULES.put("HIGH_PROTEIN", Arrays.asList("高蛋白", "蛋白质丰富"));
        NUTRITION_KEYWORD_RULES.put("LOW_FAT", Arrays.asList("低脂肪", "低脂"));
        NUTRITION_KEYWORD_RULES.put("LOW_CARB", Arrays.asList("低碳水", "低碳"));
        NUTRITION_KEYWORD_RULES.put("HIGH_FIBER", Arrays.asList("高纤维", "膳食纤维丰富"));
        NUTRITION_KEYWORD_RULES.put("LOW_SUGAR", Arrays.asList("低糖", "无添加糖"));
        NUTRITION_KEYWORD_RULES.put("LOW_SODIUM", Arrays.asList("低钠", "少盐"));
        
        // 健康关键词规则
        HEALTH_KEYWORD_RULES.put("WEIGHT_LOSS", Arrays.asList("减肥餐", "低卡"));
        HEALTH_KEYWORD_RULES.put("MUSCLE_GAIN", Arrays.asList("增肌", "健身餐"));
        HEALTH_KEYWORD_RULES.put("DIABETES", Arrays.asList("控糖", "糖尿病友好"));
        HEALTH_KEYWORD_RULES.put("HEART_HEALTH", Arrays.asList("心脏健康", "心血管友好"));
        HEALTH_KEYWORD_RULES.put("DIGESTIVE_HEALTH", Arrays.asList("肠道健康", "易消化"));
    }
    
    /**
     * 从营养成分中提取关键词
     * @param nutrition 营养成分
     * @return 关键词列表
     */
    public List<String> extractKeywordsFromNutrition(ProductIngredient.NutritionData nutrition) {
        List<String> keywords = new ArrayList<>();
        
        // 蛋白质相关关键词
        if (nutrition.getProtein() != null && nutrition.getProtein().doubleValue() >= 20) {
            keywords.add("高蛋白");
        } else if (nutrition.getProtein() != null && nutrition.getProtein().doubleValue() <= 5) {
            keywords.add("低蛋白");
        }
        
        // 脂肪相关关键词
        if (nutrition.getFat() != null && nutrition.getFat().doubleValue() <= 5) {
            keywords.add("低脂肪");
        } else if (nutrition.getFat() != null && nutrition.getFat().doubleValue() >= 20) {
            keywords.add("高脂肪");
        }
        
        // 碳水化合物相关关键词
        if (nutrition.getCarbohydrates() != null && nutrition.getCarbohydrates().doubleValue() <= 10) {
            keywords.add("低碳水");
        } else if (nutrition.getCarbohydrates() != null && nutrition.getCarbohydrates().doubleValue() >= 50) {
            keywords.add("高碳水");
        }
        
        // 纤维相关关键词
        if (nutrition.getFiber() != null && nutrition.getFiber().doubleValue() >= 5) {
            keywords.add("高纤维");
        }
        
        // 糖分相关关键词
        if (nutrition.getSugar() != null && nutrition.getSugar().doubleValue() <= 5) {
            keywords.add("低糖");
        } else if (nutrition.getSugar() != null && nutrition.getSugar().doubleValue() >= 20) {
            keywords.add("高糖");
        }
        
        // 钠含量相关关键词
        if (nutrition.getSodium() != null && nutrition.getSodium().doubleValue() <= 200) {
            keywords.add("低钠");
        } else if (nutrition.getSodium() != null && nutrition.getSodium().doubleValue() >= 1000) {
            keywords.add("高钠");
        }
        
        // 热量相关关键词
        if (nutrition.getCalories() != null) {
            if (nutrition.getCalories() <= 100) {
                keywords.add("低卡");
            } else if (nutrition.getCalories() >= 300) {
                keywords.add("高卡");
            }
        }
        
        return keywords.stream().distinct().collect(Collectors.toList());
    }
    
    /**
     * 关联关键词与产品
     * @param productId 产品ID
     * @param keywords 关键词列表
     * @param analysisId 分析ID
     */
    @Transactional
    public void associateKeywordsWithProduct(Long productId, List<String> keywords, Long analysisId) {
        if (keywords == null || keywords.isEmpty()) {
            log.info("产品 {} 没有提取到关键词", productId);
            return;
        }
        
        // 查找或创建关键词
        List<IngredientKeyword> keywordEntities = new ArrayList<>();
        for (String keywordStr : keywords) {
            IngredientKeyword keyword = keywordRepository.findByKeyword(keywordStr)
                    .orElseGet(() -> createNewKeyword(keywordStr));
            
            // 增加使用次数
            keyword.incrementUsageCount();
            keywordEntities.add(keyword);
        }
        
        // 保存关键词
        keywordRepository.saveAll(keywordEntities);
        
        // 这里应该创建ProductKeywordMapping记录
        // 由于时间关系，暂时省略具体实现
        log.info("为产品 {} 关联了 {} 个关键词: {}", productId, keywordEntities.size(), 
                keywordEntities.stream().map(IngredientKeyword::getKeyword).collect(Collectors.toList()));
    }
    
    /**
     * 创建新关键词
     * @param keyword 关键词
     * @return 关键词实体
     */
    private IngredientKeyword createNewKeyword(String keyword) {
        IngredientKeyword newKeyword = new IngredientKeyword();
        newKeyword.setKeyword(keyword);
        
        // 自动分类
        IngredientKeyword.KeywordCategory category = determineCategory(keyword);
        newKeyword.setCategory(category);
        
        // 设置描述
        String description = generateDescription(keyword, category);
        newKeyword.setDescription(description);
        
        newKeyword.setAiGenerated(true);
        newKeyword.setUsageCount(1);
        newKeyword.setPopularityScore(BigDecimal.valueOf(0.5));
        
        return newKeyword;
    }
    
    /**
     * 确定关键词分类
     * @param keyword 关键词
     * @return 分类
     */
    private IngredientKeyword.KeywordCategory determineCategory(String keyword) {
        // 营养类关键词
        if (keyword.contains("蛋白") || keyword.contains("脂肪") || keyword.contains("碳水") || 
            keyword.contains("纤维") || keyword.contains("糖") || keyword.contains("钠") || 
            keyword.contains("卡") || keyword.contains("热量")) {
            return IngredientKeyword.KeywordCategory.NUTRITION;
        }
        
        // 健康类关键词
        if (keyword.contains("健康") || keyword.contains("减肥") || keyword.contains("健身") || 
            keyword.contains("控糖") || keyword.contains("心脏") || keyword.contains("肠道")) {
            return IngredientKeyword.KeywordCategory.HEALTH;
        }
        
        // 饮食限制类关键词
        if (keyword.contains("无") || keyword.contains("低") || keyword.contains("少") || 
            keyword.contains("限制") || keyword.contains("忌口")) {
            return IngredientKeyword.KeywordCategory.DIET;
        }
        
        // 过敏原类关键词
        if (keyword.contains("过敏") || keyword.contains("不耐受") || keyword.contains("敏感")) {
            return IngredientKeyword.KeywordCategory.ALLERGEN;
        }
        
        // 默认归类为偏好
        return IngredientKeyword.KeywordCategory.PREFERENCE;
    }
    
    /**
     * 生成关键词描述
     * @param keyword 关键词
     * @param category 分类
     * @return 描述
     */
    private String generateDescription(String keyword, IngredientKeyword.KeywordCategory category) {
        switch (category) {
            case NUTRITION:
                return "营养属性关键词: " + keyword;
            case HEALTH:
                return "健康需求关键词: " + keyword;
            case DIET:
                return "饮食限制关键词: " + keyword;
            case ALLERGEN:
                return "过敏原相关关键词: " + keyword;
            case PREFERENCE:
                return "消费偏好关键词: " + keyword;
            default:
                return "通用关键词: " + keyword;
        }
    }
    
    /**
     * 获取热门关键词
     * @param limit 限制数量
     * @return 热门关键词列表
     */
    public List<IngredientKeyword> getPopularKeywords(int limit) {
        return keywordRepository.findPopularKeywords(limit);
    }
    
    /**
     * 根据分类获取关键词
     * @param category 分类
     * @return 关键词列表
     */
    public List<IngredientKeyword> getKeywordsByCategory(IngredientKeyword.KeywordCategory category) {
        return keywordRepository.findByCategory(category);
    }
    
    /**
     * 搜索关键词
     * @param query 搜索词
     * @return 关键词列表
     */
    public List<IngredientKeyword> searchKeywords(String query) {
        return keywordRepository.findByKeywordContaining(query);
    }
    
    /**
     * 获取关键词统计信息
     * @return 统计信息
     */
    public Map<String, Object> getKeywordStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // 统计各分类数量
        for (IngredientKeyword.KeywordCategory category : IngredientKeyword.KeywordCategory.values()) {
            Long count = keywordRepository.countByCategory(category);
            stats.put(category.name() + "_COUNT", count);
        }
        
        // 获取总数量
        long totalCount = keywordRepository.count();
        stats.put("TOTAL_COUNT", totalCount);
        
        // 获取最常用的关键词
        List<IngredientKeyword> topKeywords = keywordRepository.findMostUsedKeywords(10);
        stats.put("TOP_KEYWORDS", topKeywords.stream()
                .map(k -> Map.of(
                    "keyword", k.getKeyword(),
                    "usageCount", k.getUsageCount(),
                    "category", k.getCategory().name()
                ))
                .collect(Collectors.toList()));
        
        return stats;
    }
    
    /**
     * 更新关键词流行度
     * @param keywordId 关键词ID
     * @param newScore 新评分
     */
    @Transactional
    public void updateKeywordPopularity(Long keywordId, BigDecimal newScore) {
        keywordRepository.findById(keywordId).ifPresent(keyword -> {
            keyword.updatePopularityScore(newScore);
            keywordRepository.save(keyword);
            log.debug("更新关键词 {} 的流行度评分为 {}", keyword.getKeyword(), newScore);
        });
    }
    
    /**
     * 批量更新关键词使用次数
     * @param keywordIds 关键词ID列表
     */
    @Transactional
    public void batchIncrementUsageCount(List<Long> keywordIds) {
        for (Long keywordId : keywordIds) {
            keywordRepository.incrementUsageCount(keywordId);
        }
        log.info("批量更新了 {} 个关键词的使用次数", keywordIds.size());
    }
}