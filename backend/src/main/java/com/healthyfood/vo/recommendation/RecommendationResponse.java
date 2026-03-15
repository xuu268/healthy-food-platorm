package com.healthyfood.vo.recommendation;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 推荐响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 推荐场景
     */
    private String scenario;
    
    /**
     * 推荐列表
     */
    private List<RecommendationItem> recommendations;
    
    /**
     * 推荐解释
     */
    private Explanation explanation;
    
    /**
     * 生成时间
     */
    private LocalDateTime generatedAt;
    
    /**
     * 请求ID (用于追踪)
     */
    private String requestId;
    
    /**
     * 推荐算法版本
     */
    private String algorithmVersion;
    
    /**
     * 缓存状态
     */
    private CacheStatus cacheStatus;
    
    /**
     * 处理时间 (毫秒)
     */
    private Long processingTime;
    
    /**
     * 附加信息
     */
    private AdditionalInfo additionalInfo;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CacheStatus {
        private Boolean cached;
        private String cacheKey;
        private Long cacheTime;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdditionalInfo {
        private Integer totalCandidates;
        private Double averageScore;
        private Double diversityScore;
        private List<String> usedAlgorithms;
        private ModelInfo modelInfo;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModelInfo {
        private String modelName;
        private String modelVersion;
        private LocalDateTime trainedAt;
        private Double modelScore;
    }
}