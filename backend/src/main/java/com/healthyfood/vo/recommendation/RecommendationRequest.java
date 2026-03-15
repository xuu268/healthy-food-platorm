package com.healthyfood.vo.recommendation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * 推荐请求对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequest {
    
    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    /**
     * 推荐场景
     * breakfast: 早餐, lunch: 午餐, dinner: 晚餐, snack: 小吃
     * weight_loss: 减重, sugar_control: 控糖, muscle_gain: 增肌
     */
    private String scenario;
    
    /**
     * 用餐时间
     */
    private String mealTime;
    
    /**
     * 季节
     */
    private String season;
    
    /**
     * 价格范围 (元)
     */
    private PriceRange priceRange;
    
    /**
     * 用餐人数
     */
    private Integer numberOfPeople;
    
    /**
     * 特殊要求
     */
    private Set<String> specialRequirements;
    
    /**
     * 排除的菜品ID
     */
    private Set<Long> excludeDishIds;
    
    /**
     * 最大推荐数量
     */
    private Integer maxRecommendations;
    
    /**
     * 是否需要解释
     */
    private Boolean needExplanation;
    
    /**
     * 是否考虑实时数据
     */
    private Boolean considerRealTimeData;
    
    /**
     * 自定义权重
     */
    private RecommendationWeights customWeights;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceRange {
        private Double minPrice;
        private Double maxPrice;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecommendationWeights {
        private Double nutritionWeight;
        private Double tasteWeight;
        private Double healthWeight;
        private Double priceWeight;
        private Double popularityWeight;
    }
}