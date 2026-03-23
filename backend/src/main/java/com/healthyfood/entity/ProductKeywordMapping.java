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
 * 产品关键词关联实体类
 * 存储产品与关键词的关联关系
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "product_keyword_mapping")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductKeywordMapping {
    
    /**
     * 来源类型枚举
     */
    public enum SourceType {
        AI_ANALYSIS,    // AI分析
        MANUAL,         // 手动
        USER_FEEDBACK   // 用户反馈
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 产品ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    /**
     * 关键词ID
     */
    @Column(name = "keyword_id", nullable = false)
    private Long keywordId;
    
    /**
     * 置信度评分
     */
    @Column(name = "confidence_score", precision = 3, scale = 2)
    private BigDecimal confidenceScore = BigDecimal.ONE;
    
    /**
     * 来源
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "source", length = 20)
    private SourceType source = SourceType.AI_ANALYSIS;
    
    /**
     * 匹配原因
     */
    @Column(name = "match_reason", columnDefinition = "text")
    private String matchReason;
    
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
     * 关联的产品实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    /**
     * 关联的关键词实体
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", insertable = false, updatable = false)
    private IngredientKeyword keyword;
    
    /**
     * 检查置信度是否足够高
     * @param threshold 阈值
     * @return 是否足够高
     */
    public boolean isConfidenceHigh(BigDecimal threshold) {
        if (confidenceScore == null || threshold == null) {
            return false;
        }
        return confidenceScore.compareTo(threshold) >= 0;
    }
    
    /**
     * 检查是否为AI分析来源
     * @return 是否为AI分析来源
     */
    public boolean isFromAiAnalysis() {
        return source == SourceType.AI_ANALYSIS;
    }
    
    /**
     * 检查是否为手动来源
     * @return 是否为手动来源
     */
    public boolean isFromManual() {
        return source == SourceType.MANUAL;
    }
    
    /**
     * 检查是否为用户反馈来源
     * @return 是否为用户反馈来源
     */
    public boolean isFromUserFeedback() {
        return source == SourceType.USER_FEEDBACK;
    }
    
    /**
     * 获取来源描述
     * @return 来源描述
     */
    public String getSourceDescription() {
        switch (source) {
            case AI_ANALYSIS:
                return "AI智能分析";
            case MANUAL:
                return "手动标注";
            case USER_FEEDBACK:
                return "用户反馈";
            default:
                return "未知";
        }
    }
    
    /**
     * 获取置信度等级
     * @return 置信度等级 (高/中/低)
     */
    public String getConfidenceLevel() {
        if (confidenceScore == null) {
            return "低";
        }
        
        if (confidenceScore.compareTo(new BigDecimal("0.9")) >= 0) {
            return "高";
        } else if (confidenceScore.compareTo(new BigDecimal("0.7")) >= 0) {
            return "中";
        } else {
            return "低";
        }
    }
    
    /**
     * 获取置信度颜色（用于前端展示）
     * @return 颜色代码
     */
    public String getConfidenceColor() {
        String level = getConfidenceLevel();
        switch (level) {
            case "高":
                return "#4CAF50";  // 绿色
            case "中":
                return "#FF9800";  // 橙色
            case "低":
                return "#F44336";  // 红色
            default:
                return "#757575";  // 灰色
        }
    }
    
    /**
     * 更新置信度评分
     * @param newScore 新的评分
     * @param weight 权重 (0-1)
     */
    public void updateConfidenceScore(BigDecimal newScore, BigDecimal weight) {
        if (newScore == null || weight == null) {
            return;
        }
        
        if (confidenceScore == null) {
            confidenceScore = newScore;
        } else {
            // 使用加权平均更新评分
            BigDecimal remainingWeight = BigDecimal.ONE.subtract(weight);
            confidenceScore = confidenceScore.multiply(remainingWeight)
                    .add(newScore.multiply(weight));
            
            // 确保评分在0-1之间
            if (confidenceScore.compareTo(BigDecimal.ZERO) < 0) {
                confidenceScore = BigDecimal.ZERO;
            } else if (confidenceScore.compareTo(BigDecimal.ONE) > 0) {
                confidenceScore = BigDecimal.ONE;
            }
        }
    }
    
    /**
     * 根据用户反馈调整置信度
     * @param isPositive 是否为正面反馈
     */
    public void adjustByUserFeedback(boolean isPositive) {
        BigDecimal adjustment = isPositive ? new BigDecimal("0.05") : new BigDecimal("-0.05");
        BigDecimal newScore = confidenceScore.add(adjustment);
        
        // 确保评分在0-1之间
        if (newScore.compareTo(BigDecimal.ZERO) < 0) {
            newScore = BigDecimal.ZERO;
        } else if (newScore.compareTo(BigDecimal.ONE) > 0) {
            newScore = BigDecimal.ONE;
        }
        
        confidenceScore = newScore;
        
        // 更新来源为用户反馈
        source = SourceType.USER_FEEDBACK;
    }
    
    /**
     * 获取简化的关联信息
     * @return 简化信息
     */
    public SimpleMappingInfo toSimpleInfo() {
        SimpleMappingInfo info = new SimpleMappingInfo();
        info.setId(id);
        info.setProductId(productId);
        info.setKeywordId(keywordId);
        info.setConfidenceScore(confidenceScore);
        info.setSource(source);
        info.setMatchReason(matchReason);
        info.setConfidenceLevel(getConfidenceLevel());
        info.setConfidenceColor(getConfidenceColor());
        info.setSourceDescription(getSourceDescription());
        
        if (keyword != null) {
            info.setKeyword(keyword.getKeyword());
            info.setKeywordCategory(keyword.getCategory());
        }
        
        return info;
    }
    
    /**
     * 简化关联信息类
     */
    @Data
    public static class SimpleMappingInfo {
        private Long id;
        private Long productId;
        private Long keywordId;
        private String keyword;
        private IngredientKeyword.KeywordCategory keywordCategory;
        private BigDecimal confidenceScore;
        private SourceType source;
        private String matchReason;
        private String confidenceLevel;
        private String confidenceColor;
        private String sourceDescription;
        
        public SimpleMappingInfo() {
            // 默认构造函数
        }
    }
    
    /**
     * 验证关联是否有效
     * @return 是否有效
     */
    public boolean isValid() {
        return productId != null && productId > 0 && 
               keywordId != null && keywordId > 0 &&
               confidenceScore != null &&
               confidenceScore.compareTo(BigDecimal.ZERO) >= 0 &&
               confidenceScore.compareTo(BigDecimal.ONE) <= 0;
    }
}