package com.healthyfood.vo.recommendation;

import com.healthyfood.entity.Dish;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map;
import java.util.Set;

/**
 * 推荐项对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationItem {
    
    /**
     * 菜品信息
     */
    private Dish dish;
    
    /**
     * 推荐分数 (0-1)
     */
    private Double score;
    
    /**
     * 营养匹配度 (0-1)
     */
    private Double nutritionMatch;
    
    /**
     * 口味匹配度 (0-1)
     */
    private Double tasteMatch;
    
    /**
     * 健康效益 (0-1)
     */
    private Double healthBenefits;
    
    /**
     * 价格合理性 (0-1)
     */
    private Double priceReasonableness;
    
    /**
     * 流行度分数 (0-1)
     */
    private Double popularityScore;
    
    /**
     * 个性化程度 (0-1)
     */
    private Double personalizationLevel;
    
    /**
     * 推荐理由
     */
    private Set<String> reasons;
    
    /**
     * 营养成分分析
     */
    private NutritionAnalysis nutritionAnalysis;
    
    /**
     * 健康效益分析
     */
    private HealthBenefitAnalysis healthBenefitAnalysis;
    
    /**
     * 替代菜品
     */
    private Set<AlternativeDish> alternatives;
    
    /**
     * 烹饪建议
     */
    private CookingAdvice cookingAdvice;
    
    /**
     * 搭配建议
     */
    private PairingAdvice pairingAdvice;
    
    /**
     * 算法贡献
     */
    private Map<String, Double> algorithmContributions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutritionAnalysis {
        private Map<String, NutrientInfo> nutrients;
        private Double totalCalories;
        private String calorieLevel;  // low, medium, high
        private Boolean isBalanced;
        private Set<String> nutritionHighlights;
        private Set<String> nutritionWarnings;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NutrientInfo {
        private String name;
        private Double amount;
        private String unit;
        private Double dailyPercentage;
        private String level;  // low, adequate, high
        private String importance;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HealthBenefitAnalysis {
        private Set<String> primaryBenefits;
        private Set<String> secondaryBenefits;
        private Set<String> targetHealthGoals;
        private Set<String> riskReductionBenefits;
        private String overallHealthImpact;  // positive, neutral, caution
        private Set<String> specialConsiderations;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlternativeDish {
        private Long dishId;
        private String name;
        private String reason;  // lower_calorie, higher_protein, cheaper, etc.
        private Double similarityScore;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CookingAdvice {
        private String difficulty;  // easy, medium, hard
        private Integer preparationTime;  // minutes
        private Integer cookingTime;  // minutes
        private Set<String> requiredUtensils;
        private Set<String> cookingTips;
        private String bestServed;  // hot, cold, warm
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PairingAdvice {
        private Set<PairingItem> recommendedPairings;
        private Set<PairingItem> avoidPairings;
        private String mealSuggestion;
        private String servingSuggestion;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PairingItem {
        private String type;  // dish, beverage, side, dessert
        private String name;
        private String reason;
        private Double compatibilityScore;
    }
}