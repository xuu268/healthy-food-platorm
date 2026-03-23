package com.healthyfood.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 成分关键词实体类
 * 存储用于产品匹配和搜索的关键词
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "ingredient_keyword")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngredientKeyword {
    
    /**
     * 关键词分类枚举
     */
    public enum KeywordCategory {
        NUTRITION,  // 营养
        HEALTH,     // 健康
        DIET,       // 饮食
        ALLERGEN,   // 过敏原
        PREFERENCE  // 偏好
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关键词
     */
    @Column(name = "keyword", nullable = false, unique = true, length = 50)
    private String keyword;
    
    /**
     * 分类
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private KeywordCategory category;
    
    /**
     * 描述
     */
    @Column(name = "description", columnDefinition = "text")
    private String description;
    
    /**
     * AI生成标志
     */
    @Column(name = "ai_generated")
    private Boolean aiGenerated = true;
    
    /**
     * 使用次数
     */
    @Column(name = "usage_count")
    private Integer usageCount = 0;
    
    /**
     * 流行度评分
     */
    @Column(name = "popularity_score", precision = 3, scale = 2)
    private BigDecimal popularityScore = BigDecimal.ZERO;
    
    /**
     * 创建者ID
     */
    @Column(name = "created_by")
    private Long createdBy;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * 增加使用次数
     * @return 增加后的使用次数
     */
    public Integer incrementUsageCount() {
        if (usageCount == null) {
            usageCount = 0;
        }
        usageCount++;
        return usageCount;
    }
    
    /**
     * 更新流行度评分
     * @param newScore 新的评分
     */
    public void updatePopularityScore(BigDecimal newScore) {
        if (newScore != null) {
            // 使用加权平均更新评分
            if (popularityScore == null || BigDecimal.ZERO.equals(popularityScore)) {
                popularityScore = newScore;
            } else {
                // 权重：历史评分占70%，新评分占30%
                popularityScore = popularityScore.multiply(new BigDecimal("0.7"))
                        .add(newScore.multiply(new BigDecimal("0.3")));
            }
        }
    }
    
    /**
     * 获取分类描述
     * @return 分类描述
     */
    public String getCategoryDescription() {
        switch (category) {
            case NUTRITION:
                return "营养属性";
            case HEALTH:
                return "健康需求";
            case DIET:
                return "饮食限制";
            case ALLERGEN:
                return "过敏原";
            case PREFERENCE:
                return "消费偏好";
            default:
                return "其他";
        }
    }
    
    /**
     * 获取关键词类型（用于前端展示）
     * @return 关键词类型
     */
    public String getKeywordType() {
        switch (category) {
            case NUTRITION:
            case HEALTH:
                return "benefit";  // 益处类
            case DIET:
            case ALLERGEN:
                return "restriction";  // 限制类
            case PREFERENCE:
                return "preference";  // 偏好类
            default:
                return "other";
        }
    }
    
    /**
     * 检查是否为AI生成
     * @return 是否为AI生成
     */
    public boolean isAiGenerated() {
        return Boolean.TRUE.equals(aiGenerated);
    }
    
    /**
     * 检查是否为手动创建
     * @return 是否为手动创建
     */
    public boolean isManualCreated() {
        return !isAiGenerated();
    }
    
    /**
     * 获取流行度等级
     * @return 流行度等级 (1-5)
     */
    public int getPopularityLevel() {
        if (popularityScore == null) {
            return 1;
        }
        
        if (popularityScore.compareTo(new BigDecimal("0.8")) >= 0) {
            return 5;  // 非常流行
        } else if (popularityScore.compareTo(new BigDecimal("0.6")) >= 0) {
            return 4;  // 比较流行
        } else if (popularityScore.compareTo(new BigDecimal("0.4")) >= 0) {
            return 3;  // 一般流行
        } else if (popularityScore.compareTo(new BigDecimal("0.2")) >= 0) {
            return 2;  // 较少使用
        } else {
            return 1;  // 很少使用
        }
    }
    
    /**
     * 获取流行度描述
     * @return 流行度描述
     */
    public String getPopularityDescription() {
        int level = getPopularityLevel();
        switch (level) {
            case 5:
                return "非常流行";
            case 4:
                return "比较流行";
            case 3:
                return "一般流行";
            case 2:
                return "较少使用";
            case 1:
                return "很少使用";
            default:
                return "未知";
        }
    }
    
    /**
     * 获取关键词的图标（用于前端展示）
     * @return 图标名称
     */
    public String getIconName() {
        switch (category) {
            case NUTRITION:
                return "nutrition";
            case HEALTH:
                return "health";
            case DIET:
                return "diet";
            case ALLERGEN:
                return "warning";
            case PREFERENCE:
                return "star";
            default:
                return "tag";
        }
    }
    
    /**
     * 获取关键词的颜色（用于前端展示）
     * @return 颜色代码
     */
    public String getColorCode() {
        switch (category) {
            case NUTRITION:
                return "#4CAF50";  // 绿色
            case HEALTH:
                return "#2196F3";  // 蓝色
            case DIET:
                return "#FF9800";  // 橙色
            case ALLERGEN:
                return "#F44336";  // 红色
            case PREFERENCE:
                return "#9C27B0";  // 紫色
            default:
                return "#757575";  // 灰色
        }
    }
    
    /**
     * 验证关键词是否有效
     * @return 是否有效
     */
    public boolean isValid() {
        return keyword != null && !keyword.trim().isEmpty() && keyword.length() <= 50;
    }
    
    /**
     * 获取简化的关键词信息（用于API响应）
     * @return 简化信息
     */
    public SimpleKeywordInfo toSimpleInfo() {
        SimpleKeywordInfo info = new SimpleKeywordInfo();
        info.setId(id);
        info.setKeyword(keyword);
        info.setCategory(category);
        info.setDescription(description);
        info.setPopularityScore(popularityScore);
        info.setUsageCount(usageCount);
        return info;
    }
    
    /**
     * 简化关键词信息类
     */
    @Data
    public static class SimpleKeywordInfo {
        private Long id;
        private String keyword;
        private KeywordCategory category;
        private String description;
        private BigDecimal popularityScore;
        private Integer usageCount;
        private String categoryDescription;
        private String keywordType;
        private String iconName;
        private String colorCode;
        
        public SimpleKeywordInfo() {
            // 默认构造函数
        }
    }
}